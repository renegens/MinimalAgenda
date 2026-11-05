package platform

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

actual fun uses24HourClock(): Boolean {
    dateTimeContext()?.let { return DateFormat.is24HourFormat(it) }
    val pattern = (java.text.DateFormat.getTimeInstance(
        java.text.DateFormat.SHORT, Locale.getDefault()
    ) as SimpleDateFormat).toPattern()
    return patternUses24HourClock(pattern)
}
