package platform

import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

actual fun uses24HourClock(): Boolean {
    val pattern = NSDateFormatter.dateFormatFromTemplate("j", 0u, NSLocale.currentLocale).orEmpty()
    return patternUses24HourClock(pattern)
}
