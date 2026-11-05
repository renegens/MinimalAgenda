package platform

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class Browser(private val context: Context) {

    actual fun openUrl(url: String) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(context.applicationContext.packageManager) != null) {
            context.startActivity(intent)
       }
    }
}