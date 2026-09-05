package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.MatrixClient
import de.connect2x.trixnity.client.room
import de.connect2x.trixnity.client.room.getState
import de.connect2x.trixnity.client.user
import de.connect2x.trixnity.client.user.getAccountData
import de.connect2x.trixnity.core.model.RoomId
import de.connect2x.trixnity.core.model.events.m.room.EncryptedFile
import de.connect2x.trixnity.core.model.events.m.room.ImageInfo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/** 一張可發送的貼圖（含加密房需要的 EncryptedFile 金鑰）。 */
data class StickerItem(
    val shortcode: String,
    val body: String,
    val mxcUrl: String?,
    val file: EncryptedFile?,
    val info: ImageInfo?,
)

/** 一個貼圖包；roomId 指向來源房間（個人包為 null），UI 顯示名稱用。 */
data class StickerPack(
    val name: String,
    val roomId: RoomId?,
    val stickers: List<StickerItem>,
)

/**
 * MSC2545 貼圖包門面（對齊 maunium stickerpicker 的寫入格式）：
 * - 個人包：global account data `im.ponies.user_emotes`（images 直存）
 * - 房間包：`im.ponies.emote_rooms` 引用房間，每包是該房一個
 *   `im.ponies.room_emotes` state 事件（state_key = 包短名，`pack.display_name` 是顯示名）
 *
 * 貼圖倉庫房「Sticker Vault」就是這種結構：一房 82 包。
 */
class StickerRepository(private val client: MatrixClient) {

    fun packs(): Flow<List<StickerPack>> {
        val userService = client.user
        val personal = userService.getAccountData<EmoteImagesContent>()
            .map { content -> listOfNotNull(personalPack(content)) }
        val roomPacks = userService.getAccountData<EmoteRoomsContent>()
            .flatMapLatest { emoteRooms -> mergeRoomPacks(emoteRooms?.rooms.orEmpty()) }
        return combine(personal, roomPacks) { mine, rooms ->
            (mine + rooms).filter { it.stickers.isNotEmpty() }
        }
    }

    private fun personalPack(content: EmoteImagesContent?): StickerPack? {
        val images = content?.images.orEmpty()
        if (images.isEmpty()) return null
        return StickerPack(
            name = StickerRepository.PersonalPackName,
            roomId = null,
            stickers = images.mapNotNull { (shortcode, image) ->
                image.toSticker(shortcode, usableAsSticker(packUsage = null, imageUsage = image.usage))
            },
        )
    }

    /**
     * emote_rooms 的 rooms 是 `roomId → state_key → {}`。每個 state_key 是一包，
     * 逐個讀該房的 state 事件；82 包 = 82 個 state 流，combine 收斂。
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun mergeRoomPacks(
        rooms: Map<String, Map<String, kotlinx.serialization.json.JsonObject>>?,
    ): Flow<List<StickerPack>> {
        val references = rooms.orEmpty()
            .flatMap { (roomId, stateKeys) ->
                stateKeys.keys.map { it to roomId }
            }
            .distinct()
        if (references.isEmpty()) return flowOf(emptyList())
        val perPack = references.map { (stateKey, roomIdStr) ->
            val roomId = RoomId(roomIdStr)
            client.room.getState<RoomEmotesContent>(roomId, stateKey)
                .map { event ->
                    val content = event?.content
                    val images = content?.images.orEmpty().filter { (_, image) ->
                        usableAsSticker(packUsage = content?.pack?.usage, imageUsage = image.usage)
                    }
                    if (images.isEmpty()) null
                    else StickerPack(
                        name = content?.pack?.displayName?.takeIf { it.isNotBlank() }
                            ?: stateKey,
                        roomId = roomId,
                        stickers = images.mapNotNull { (shortcode, image) ->
                            image.toSticker(shortcode, usable = true)
                        },
                    )
                }
        }
        return combine(perPack) { packs -> packs.filterNotNull() }
    }

    /** 發送 m.sticker；加密房由 Trixnity outbox 走 megolm（content 已註冊進 mappings）。回 eventId 便於撤回。 */
    suspend fun sendSticker(roomId: RoomId, sticker: StickerItem): Result<String> = runCatching {
        client.room.sendMessage(roomId) {
            content(
                StickerEventContent(
                    body = sticker.body.ifBlank { sticker.shortcode },
                    url = sticker.mxcUrl,
                    file = sticker.file,
                    info = sticker.info,
                ),
            )
        }
    }

    companion object {
        const val PersonalPackName = "ponies"
    }
}

private fun EmoteImage.toSticker(shortcode: String, usable: Boolean): StickerItem? {
    if (!usable) return null
    if (url == null && file == null) return null
    return StickerItem(
        shortcode = shortcode,
        body = body.ifBlank { shortcode },
        mxcUrl = url,
        file = file,
        info = info,
    )
}

/**
 * MSC2545 的 usage 過濾：包級缺省＝sticker+emoticon 都行；圖片級 usage 覆蓋包級。
 * 只標 emoticon 的條目（純表情符號）不進 Discord 式貼圖面板。
 */
private fun usableAsSticker(packUsage: List<String>?, imageUsage: List<String>?): Boolean {
    val usage = imageUsage ?: packUsage ?: return true
    return "sticker" in usage
}
