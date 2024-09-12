package platform

import platform.Foundation.*
import platform.Foundation.NSLocale
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDate

actual fun formatTimestamp(timestamp: Long): String {
    val nsTimeInterval = (timestamp / 1000).toDouble()
    val nsDate = NSDate.dateWithTimeIntervalSince1970(nsTimeInterval)

    val customFormat = "dd/MM/yyyy"
    val dateFormat = NSDateFormatter.dateFormatFromTemplate(
        tmplate = customFormat,
        options = 0u,
        locale = NSLocale.currentLocale
    )

    val formatter = NSDateFormatter()
    formatter.dateFormat = dateFormat!!

    return formatter.stringFromDate(nsDate)
}