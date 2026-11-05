package platform

import kotlinx.datetime.LocalDate
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeZoneForSecondsFromGMT

private fun wallClockFormatter(): NSDateFormatter = NSDateFormatter().apply {
    locale = NSLocale.currentLocale
    timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
}

actual fun formatLocalDate(date: LocalDate): String {
    val formatter = wallClockFormatter()
    formatter.setLocalizedDateFormatFromTemplate("yMd")
    return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(date.toEpochDays() * 86_400.0))
}

actual fun formatDayAndMonth(day: Int, month: Int): String {
    val pattern = NSDateFormatter.dateFormatFromTemplate("Md", 0u, NSLocale.currentLocale).orEmpty()
    return compactDayAndMonth(day, month, pattern)
}

actual fun formatLocalTime(hour: Int, minute: Int): String {
    val formatter = wallClockFormatter()
    formatter.timeStyle = NSDateFormatterShortStyle
    return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970((hour * 60.0 + minute) * 60))
}

actual fun formatMonthName(month: Int, abbreviated: Boolean): String {
    val formatter = wallClockFormatter()
    formatter.dateFormat = if (abbreviated) "LLL" else "LLLL"
    val date = LocalDate(2000, month, 1)
    return formatter.stringFromDate(NSDate.dateWithTimeIntervalSince1970(date.toEpochDays() * 86_400.0))
}
