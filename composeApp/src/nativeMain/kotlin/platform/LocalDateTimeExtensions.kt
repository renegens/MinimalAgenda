package platform

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSTimeZone
import platform.Foundation.localTimeZone

fun LocalDateTime.toEKEventDate(): NSDate {
    val nsCalendar = NSCalendar.currentCalendar
    nsCalendar.timeZone = NSTimeZone.localTimeZone
    return nsCalendar.dateFromComponents(this.toNSDateComponents()) ?: NSDate()
}