package platform

import data.APP_DISPLAY_NAME
import data.iosSupportEmail
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication

actual fun sendEmail(onError: () -> Unit) {
    val components = NSURLComponents().apply {
        scheme = "mailto"
        path = iosSupportEmail
        queryItems = listOf(NSURLQueryItem("subject", "Support for $APP_DISPLAY_NAME ${getBuildVersion()}"))
    }
    val mailtoUrl = components.URL
    if (mailtoUrl == null || !UIApplication.sharedApplication.canOpenURL(mailtoUrl)) {
        onError()
        return
    }
    UIApplication.sharedApplication.openURL(
        url = mailtoUrl,
        options = emptyMap<Any?, Any>(),
        completionHandler = { opened -> if (!opened) onError() },
    )
}
