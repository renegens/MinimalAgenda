package platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import data.APP_DISPLAY_NAME
import data.iosSupportEmail
import org.koin.mp.KoinPlatform

actual fun sendEmail(onError: () -> Unit) {
    val context = KoinPlatform.getKoin().get<Context>()
    val subject = Uri.encode("Support for $APP_DISPLAY_NAME ${getBuildVersion()}")
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$iosSupportEmail?subject=$subject"))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        onError()
    } catch (_: SecurityException) {
        onError()
    }
}
