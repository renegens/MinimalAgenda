package platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

actual class Browser(private val context: Context) {

    actual fun openUrl(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        // Starting the intent works without querying other installed packages first.
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // The device has no app that can open this link.
        }
    }
}