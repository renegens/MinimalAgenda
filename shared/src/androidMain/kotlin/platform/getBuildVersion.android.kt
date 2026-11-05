package platform

import android.content.Context
import data.emptyReleaseVersion
import org.koin.mp.KoinPlatform

actual fun getBuildVersion(): String {
    val context = KoinPlatform.getKoin().get<Context>()
    val version = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (_: Exception) {
        null
    }
    return version.takeUnless { it.isNullOrEmpty() } ?: emptyReleaseVersion
}
