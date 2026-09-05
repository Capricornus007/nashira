package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.client.createDefaultEventContentSerializerMappingsModule
import de.connect2x.trixnity.client.createTrixnityDefaultModuleFactories
import de.connect2x.trixnity.core.model.events.EphemeralDataUnitContent
import de.connect2x.trixnity.core.model.events.EphemeralEventContent
import de.connect2x.trixnity.core.model.events.GlobalAccountDataEventContent
import de.connect2x.trixnity.core.model.events.MessageEventContent
import de.connect2x.trixnity.core.model.events.RoomAccountDataEventContent
import de.connect2x.trixnity.core.model.events.StateEventContent
import de.connect2x.trixnity.core.model.events.ToDeviceEventContent
import de.connect2x.trixnity.core.model.events.m.Mentions
import de.connect2x.trixnity.core.model.events.m.RelatesTo
import de.connect2x.trixnity.core.model.events.m.room.EncryptedFile
import de.connect2x.trixnity.core.model.events.m.room.ImageInfo
import de.connect2x.trixnity.core.serialization.events.EventContentSerializerMapping
import de.connect2x.trixnity.core.serialization.events.EventContentSerializerMappingImpl
import de.connect2x.trixnity.core.serialization.events.EventContentSerializerMappings
import de.connect2x.trixnity.core.serialization.events.MessageEventContentSerializerMapping
import de.connect2x.trixnity.core.serialization.events.StateEventContentSerializerMapping
import de.connect2x.trixnity.core.serialization.events.default
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.dsl.module

/**
 * MSC2545（Image collections / emotes & stickers）的線上格式，對齊
 * maunium stickerpicker 同步腳本與 Cinny/Nheko/FluffyChat 的實際寫入：
 *
 * - `im.ponies.user_emotes`（global account data）：個人包，`images` 直存
 * - `im.ponies.emote_rooms` / `m.image_pack.rooms`（global account data）：
 *   `rooms: {"!roomid:server": {"pack_state_key": {}}}` —— 引用哪個房間的哪些包
 * - `im.ponies.room_emotes` / `m.room.image_pack`（房間 state，**每包一個 state 事件**，
 *   state_key = 包短名）：`pack: {display_name, usage, avatar_url}` + `images`
 *   （貼圖包倉庫房就是這種結構，82 包 = 82 個 state 事件）
 * 發送則是頂層 `m.sticker` 房間事件（content 同 m.image；加密房走 megolm）。
 *
 * 這些類型 Trixnity 沒有內建，必須自己聲明並註冊進 EventContentSerializerMappings——
 * 註冊除了讓序列化可用，也會透過 mappings 的 eventTypesHash 進 sync filter，
 * 沒註冊的話 sync 根本不會把這幾種 account data 下發下來。
 */
@Serializable
data class EmoteImage(
    @SerialName("url") val url: String? = null,
    @SerialName("file") val file: EncryptedFile? = null,
    @SerialName("info") val info: ImageInfo? = null,
    @SerialName("body") val body: String = "",
    @SerialName("usage") val usage: List<String>? = null,
)

/** 頂層 `m.sticker` 房間事件的 content。 */
@Serializable
data class StickerEventContent(
    @SerialName("body") val body: String,
    @SerialName("url") val url: String? = null,
    @SerialName("file") val file: EncryptedFile? = null,
    @SerialName("info") val info: ImageInfo? = null,
    @SerialName("m.relates_to") override val relatesTo: RelatesTo? = null,
    @SerialName("m.mentions") override val mentions: Mentions? = null,
    @SerialName("external_url") override val externalUrl: String? = null,
) : MessageEventContent {
    override fun copyWith(relatesTo: RelatesTo?): MessageEventContent = copy(relatesTo = relatesTo)
}

/** `im.ponies.user_emotes`（個人包）的 images 映射。 */
@Serializable
data class EmoteImagesContent(
    @SerialName("images") val images: Map<String, EmoteImage>? = null,
) : GlobalAccountDataEventContent

/** 貼圖包元數據（state 事件的 `pack` 欄位）。 */
@Serializable
data class EmotePackMeta(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("usage") val usage: List<String>? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)

/**
 * `im.ponies.room_emotes` / `m.room.image_pack`（房間 state 事件，一個 state_key 一個包）。
 * `emote_rooms` 引用的 rooms 內層 key 就是這裡的 state_key。
 */
@Serializable
data class RoomEmotesContent(
    @SerialName("pack") val pack: EmotePackMeta? = null,
    @SerialName("images") val images: Map<String, EmoteImage>? = null,
    @SerialName("external_url") override val externalUrl: String? = null,
) : StateEventContent

/**
 * `im.ponies.emote_rooms` / `m.image_pack.rooms`：全域啟用哪些房間的哪些包。
 * rooms 是 dict（roomId → state_key → {}），不是 list——maunium 同步腳本與
 * Cinny/Nheko 都寫 dict，寫 list 會讓其他客戶端讀不到。
 */
@Serializable
data class EmoteRoomsContent(
    @SerialName("rooms") val rooms: Map<String, Map<String, kotlinx.serialization.json.JsonObject>>? = null,
) : GlobalAccountDataEventContent

/** Ponies 額外的序列化映射（unstable `im.ponies.*` + 穩定 `m.*image_pack`，其餘類別全空）。 */
private val PoniesEventContentSerializerMappings = object : EventContentSerializerMappings {
    override val message = setOf(
        MessageEventContentSerializerMapping("m.sticker", StickerEventContent::class, StickerEventContent.serializer()),
    )
    override val state = setOf(
        StateEventContentSerializerMapping("im.ponies.room_emotes", RoomEmotesContent::class, RoomEmotesContent.serializer()),
        StateEventContentSerializerMapping("m.room.image_pack", RoomEmotesContent::class, RoomEmotesContent.serializer()),
    )
    override val globalAccountData: Set<EventContentSerializerMapping<GlobalAccountDataEventContent>> = setOf(
        EventContentSerializerMappingImpl<GlobalAccountDataEventContent>(
            "im.ponies.user_emotes",
            EmoteImagesContent::class,
            EmoteImagesContent.serializer(),
        ),
        EventContentSerializerMappingImpl<GlobalAccountDataEventContent>(
            "im.ponies.emote_rooms",
            EmoteRoomsContent::class,
            EmoteRoomsContent.serializer(),
        ),
        EventContentSerializerMappingImpl<GlobalAccountDataEventContent>(
            "m.image_pack.rooms",
            EmoteRoomsContent::class,
            EmoteRoomsContent.serializer(),
        ),
    )
    override val ephemeral: Set<EventContentSerializerMapping<EphemeralEventContent>> = emptySet()
    override val ephemeralDataUnit: Set<EventContentSerializerMapping<EphemeralDataUnitContent>> = emptySet()
    override val toDevice: Set<EventContentSerializerMapping<ToDeviceEventContent>> = emptySet()
    override val roomAccountData: Set<EventContentSerializerMapping<RoomAccountDataEventContent>> = emptySet()
    override val block: Set<de.connect2x.trixnity.core.serialization.events.EventContentBlockSerializerMapping<*>> = emptySet()
}

/**
 * 取代 Trixnity 預設 mappings 的模組：預設 + Ponies。
 * 用法是從 modulesFactories 移除 `createDefaultEventContentSerializerMappingsModule` 後追加本模組，
 * 避免 koin 註冊衝突（Trixnity 自己的 bot 模式也是整串替換 modulesFactories）。
 */
fun createPoniesEventContentSerializerMappingsModule() = module {
    single<EventContentSerializerMappings> {
        EventContentSerializerMappings.default + PoniesEventContentSerializerMappings
    }
}

/** 把 Ponies mappings 接進 Trixnity 預設模組清單（給 MatrixClient.create 的 configuration 用）。 */
fun withPoniesEventContentMappings(
    factories: List<() -> org.koin.core.module.Module>,
): List<() -> org.koin.core.module.Module> =
    factories.filterNot { it == ::createDefaultEventContentSerializerMappingsModule } +
        listOf(::createPoniesEventContentSerializerMappingsModule)

/** 便捷包裝：預設模組清單 + Ponies mappings。 */
fun trixnityModuleFactoriesWithPonies(): List<() -> org.koin.core.module.Module> =
    withPoniesEventContentMappings(createTrixnityDefaultModuleFactories())
