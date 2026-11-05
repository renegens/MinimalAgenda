package platform

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class Browser {
    actual fun openUrl(url: String) {
        val urlToOpen = NSURL(string = url)
        UIApplication.sharedApplication()
            .openURL(url = urlToOpen, options = emptyMap<Any?, Any>(), completionHandler = {})
        // UIApplication.sharedApplication().openURL(urlToOpen)
    }
}