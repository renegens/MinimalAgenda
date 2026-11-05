package platform

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970

/** Kotlin dates are Gregorian; converting through an instant avoids the user's alternate NSCalendar. */
fun LocalDateTime.toEKEventDate(): NSDate =
    NSDate.dateWithTimeIntervalSince1970(toInstant(TimeZone.currentSystemDefault()).epochSeconds.toDouble())
