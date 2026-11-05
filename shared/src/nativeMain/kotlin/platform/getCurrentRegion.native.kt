package platform

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.regionCode

actual fun getCurrentRegion(): String? {
    return NSLocale.currentLocale().regionCode
}