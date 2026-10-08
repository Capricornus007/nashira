package io.github.capricornus007.nashira.matrix

import de.connect2x.trixnity.core.model.EventId

/**
 * Telegram 式相簿的併組。
 *
 * **為什麼要自己推**：我原本以為橋站會給一個相簿 ID 可以直接讀，實查結果是——
 * `grouped_id` 只在 Telegram 自己的協議層（`mautrix/telegram` 的 `pkg/gotd/`）出現，
 * **從未寫進送進 Matrix 的事件內容**；GitHub 全碼搜尋 `org:mautrix` 命中 7 檔全是生成碼。
 * 真正在推的標準是 MSC4274（Element X／ruma 已實作 `dm.filament.gallery`），
 * 但它是「一則事件塞整組」，跟橋站「拆成好幾則」完全是兩條路，而且明文交給客戶端自己判斷。
 * 所以他群裡那些相簿，到我們手上就是「同一個人、幾乎同一秒連發的幾則圖片訊息」。
 *
 * 併組條件全部有實測依據（他本機 41,346 則事件的唯讀掃描）：
 * - **相鄰**：時間線上緊貼著、中間夾了別的訊息就斷組（夾進來的那則一定會有 TimelineMessage，
 *   所以「清單裡相鄰」等價於「時間線上相鄰」）。
 * - **同一發送者**。
 * - **時間差 ≤ 3 秒**：t2bot.io（他在用的 Telegram 橋）的相簿內相鄰兩則，
 *   52/67 對落在 3 秒內；10–60 秒那 12 對是「過一陣子又發一張」，不該併。
 * - **一組最多 10 張**：照 tdesktop `history_view_media_grouped.h:22` 的 `kMaxSize = 10`；
 *   超量就切成下一組，不隱藏任何一張。
 */

/** 一組的上限（Telegram 自己也是一組 10 張）。 */
internal const val AlbumMaxItems = 10

/** 組內相鄰兩則允许的最大時間差。 */
internal const val AlbumWindowMillis = 3_000L

/**
 * 「body 不是說明文字」的那一類值。橋站與客戶端在沒有說明文字時會填佔位詞，
 * 直接拿它當相簿說明會顯示成「image」這種怪東西：
 * - Go 版 mautrix/telegram 對照片寫死 `"image"`（`tomatrix.go:568-588`，
 *   另有 `disappearing_image`／`live_photo`），檔案類寫檔名或 `file_document` 等；
 * - 舊 Python 版與 Element 上传時常用 `Photo`／`IMG_1234.JPG` 這種檔名。
 * 所以除了這些字面詞，**看起來像檔名的也一律不算說明文字**（結尾是媒體副檔名）。
 */
private val PlaceholderBodies = setOf(
    "image", "images", "photo", "photos", "picture", "video", "gif", "file", "document",
    "sticker", "audio", "voice", "voice message", "disappearing_image", "live_photo",
    "file_document", "video_document", "audio_document", "m.image", "m.video", "m.file",
)

private val MediaFileExtensions = setOf(
    "jpg", "jpeg", "png", "gif", "webp", "heic", "heif", "avif", "bmp", "tiff", "svg",
    "mp4", "mov", "m4v", "webm", "mkv", "avi", "3gp",
)

/** 這則訊息的 body 能不能當整組的說明文字。 */
internal fun looksLikeAlbumCaption(body: String): Boolean {
    val s = body.trim()
    if (s.isEmpty()) return false
    if (s.lowercase() in PlaceholderBodies) return false
    val lastDot = s.lastIndexOf('.')
    if (lastDot > 0 && lastDot >= s.length - 5) {
        if (s.substring(lastDot + 1).lowercase() in MediaFileExtensions) return false
    }
    // 纯檔名（沒有空白也沒有句子的樣子）多半也是佔位，例如 `pasted_image_1728394`
    if (s.length <= 24 && !s.contains(' ') && !s.contains('\n')) return false
    return true
}

/** 只有「純媒體、不是回覆、不是本機回顯」的訊息才有資格進相簿。 */
private fun TimelineMessage.albumEligible(): Boolean {
    if (pending || sendError != null) return false
    if (replyToEventId != null) return false
    val body = body as? MessageBody.Image ?: return false
    // 貼圖不進相簿：它是獨立的一类，Telegram 也是分開顯示
    return !body.isSticker
}

/** 把同一組的反應合併到區塊頭那一則（同一表情在多張上按過就加總）。 */
private fun mergeReactions(run: List<TimelineMessage>): Map<String, ReactionInfo> {
    val merged = LinkedHashMap<String, ReactionInfo>()
    run.forEach { msg ->
        msg.reactions.forEach { (key, info) ->
            val previous = merged[key]
            merged[key] = ReactionInfo(
                count = (previous?.count ?: 0) + info.count,
                mine = previous?.mine ?: info.mine,
            )
        }
    }
    return merged
}

private fun albumOf(run: List<TimelineMessage>): TimelineMessage {
    val images = run.map { it.body as MessageBody.Image }
    val captioned = images.filter { looksLikeAlbumCaption(it.caption) }
    // Telegram 的規則（tdesktop `itemForText()`）：整組**恰好一項**有說明文字才顯示；
    // 每張各附一句的話就不顯示，否則我們會把某張的說明擺在整組下面，看著像錯配。
    val caption = if (captioned.size == 1) captioned.single().caption.trim() else ""
    val head = run.first()
    return head.copy(
        body = MessageBody.Album(items = images, caption = caption),
        reactions = mergeReactions(run),
        pinned = run.any { it.pinned },
        albumMemberIds = run.mapNotNull { it.eventId },
    )
}

/**
 * 把相鄰同人的連發媒體併成相簿區塊。傳入順序必須是**舊→新**
 * （時間差要往後加，順序反了會全部判斷錯）。
 */
internal fun groupMediaAlbums(oldToNew: List<TimelineMessage>): List<TimelineMessage> {
    if (oldToNew.size < 2) return oldToNew
    val out = ArrayList<TimelineMessage>(oldToNew.size)
    var start = 0
    while (start < oldToNew.size) {
        var end = start
        if (oldToNew[start].albumEligible()) {
            while (end + 1 < oldToNew.size && end + 1 - start < AlbumMaxItems) {
                val prev = oldToNew[end]
                val next = oldToNew[end + 1]
                if (!next.albumEligible()) break
                if (next.sender != prev.sender) break
                if (next.timestamp - prev.timestamp > AlbumWindowMillis) break
                end++
            }
        }
        val run = oldToNew.subList(start, end + 1)
        if (run.size >= 2) out += albumOf(run) else out += run
        start = end + 1
    }
    return out
}

/** 回覆／已讀要能找到「併進區塊裡」的某一則。 */
internal fun TimelineMessage.containsEvent(id: EventId): Boolean =
    eventId == id || id in albumMemberIds
