package platform

import android.content.Context
import android.text.format.DateFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import org.koin.core.context.GlobalContext
import java.time.Month
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import java.util.TimeZone

actual fun formatLocalDate(date: LocalDate): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
        .withLocale(Locale.getDefault()).format(date.toJavaLocalDate())

actual fun formatDayAndMonth(day: Int, month: Int): String {
    val pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
        FormatStyle.SHORT, null, IsoChronology.INSTANCE, Locale.getDefault()
    )
    return compactDayAndMonth(day, month, pattern)
}

actual fun formatLocalTime(hour: Int, minute: Int): String {
    val context = dateTimeContext()
    val formatter = if (context != null) DateFormat.getTimeFormat(context)
    else java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT, Locale.getDefault())
    // A synthetic UTC date keeps these wall-clock components unchanged by the system time zone.
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date((hour * 60L + minute) * 60_000L))
}

actual fun formatMonthName(month: Int, abbreviated: Boolean): String =
    Month.of(month).getDisplayName(
        if (abbreviated) TextStyle.SHORT_STANDALONE else TextStyle.FULL_STANDALONE,
        Locale.getDefault()
    )

/** No Android context exists in common host tests; use the locale default in that case. */
internal fun dateTimeContext(): Context? = GlobalContext.getOrNull()?.getOrNull<Context>()
