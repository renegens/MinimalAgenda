package platform

import data.iosSupportEmail
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun sendEmail(onError: () -> Unit) {
    val mailtoString = "mailto:${iosSupportEmail}?subject=Support for Papely ${getBuildVersion()}"
    val mailtoUrl = NSURL(string = mailtoString)
    if (UIApplication.sharedApplication.canOpenURL(mailtoUrl)) {
        UIApplication.sharedApplication.openURL(mailtoUrl)
    } else {
        onError.invoke()
    }
}