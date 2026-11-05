package platform

import data.emptyReleaseVersion
import platform.Foundation.NSBundle

actual fun getBuildVersion(): String {
    val nsObject: Any? = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")
    val version = nsObject.toString()
    return version.ifEmpty { emptyReleaseVersion }
}