package platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import org.koin.mp.KoinPlatform

actual fun openSettings() {
    val context = KoinPlatform.getKoin().get<Context>()
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
