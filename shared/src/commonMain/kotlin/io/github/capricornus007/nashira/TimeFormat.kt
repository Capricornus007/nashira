package io.github.capricornus007.nashira

import io.github.capricornus007.nashira.i18n.Strings
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime

/** 時間線用的本地時間換算：訊息時戳是 epoch 毫秒。 */
private fun localDateTimeOf(epochMillis: Long): LocalDateTime =
    Instant.fromEpochMilliseconds(epochMillis).toLocalDateTime(TimeZone.currentSystemDefault())

fun localDateOf(epochMillis: Long): LocalDate = localDateTimeOf(epochMillis).date

/** 訊息標頭的時刻，如 15:26。 */
fun formatClock(epochMillis: Long): String {
    val time = localDateTimeOf(epochMillis)
    return "${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}"
}

/** 日期分隔線文字：今天／昨天／完整日期。 */
fun formatDateDivider(epochMillis: Long, today: LocalDate, strings: Strings): String {
    val date = localDateOf(epochMillis)
    val dayDiff = today.toEpochDays() - date.toEpochDays()
    return when (dayDiff) {
        0L -> strings.today
        1L -> strings.yesterday
        else -> strings.formatDate(date.year, date.month.number, date.day)
    }
}

/** 聊天室清單右側的相對時間，對齊 Discord 的「17 天／1 個月前」。 */
fun formatRelative(epochMillis: Long, nowMillis: Long, strings: Strings): String {
    if (epochMillis <= 0L) return ""
    val minutes = ((nowMillis - epochMillis) / 60_000L).coerceAtLeast(0L)
    return when {
        minutes < 1L -> strings.justNow
        minutes < 60L -> strings.minutesAgo.replace("%d", minutes.toString())
        minutes < 60L * 24L -> strings.hoursAgo.replace("%d", (minutes / 60L).toString())
        minutes < 60L * 24L * 30L -> strings.daysAgo.replace("%d", (minutes / (60L * 24L)).toString())
        minutes < 60L * 24L * 365L -> strings.monthsAgo.replace("%d", (minutes / (60L * 24L * 30L)).toString())
        else -> strings.yearsAgo.replace("%d", (minutes / (60L * 24L * 365L)).toString())
    }
}
