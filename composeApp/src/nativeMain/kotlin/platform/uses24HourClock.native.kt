package platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun uses24HourClock(): Boolean {
    val date = NSDate()
    val nsDateFormatter = NSDateFormatter()
    nsDateFormatter.locale = NSLocale.currentLocale
    nsDateFormatter.timeStyle = NSDateFormatterShortStyle
    val localTimeString = nsDateFormatter.stringFromDate(date)
    return localTimeString.last().isDigit()
}