package platform

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toNSDateComponents
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate

fun LocalDateTime.toEKEventDate(): NSDate {
    return NSCalendar.currentCalendar.dateFromComponents(this.toNSDateComponents()) ?: NSDate()
}