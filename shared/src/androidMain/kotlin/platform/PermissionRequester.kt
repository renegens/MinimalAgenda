package platform

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CompletableDeferred

/**
 * Bridge between suspend code and the Activity Result API. The Activity calls [register] in `onCreate`
 * (the launcher must be registered before the Activity is STARTED) and [unregister] in `onDestroy`,
 * so no Activity reference outlives it.
 */
class PermissionRequester {
    private var activity: ComponentActivity? = null
    private var launcher: ActivityResultLauncher<Array<String>>? = null
    private var pending: CompletableDeferred<Map<String, Boolean>>? = null

    fun register(activity: ComponentActivity) {
        this.activity = activity
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result ->
            pending?.complete(result)
            pending = null
        }
    }

    fun unregister(activity: ComponentActivity) {
        if (this.activity !== activity) return
        // The launcher is lifecycle-owned and cleaned up by the Activity; unregistering it here would drop
        // a result that arrives after a configuration change.
        launcher = null
        this.activity = null
        // A configuration change re-registers and still receives the result; only drop on real finish.
        if (activity.isFinishing) {
            pending?.cancel()
            pending = null
        }
    }

    /** True when the system will still show a dialog explanation for [permission] (denied once, not permanently). */
    fun shouldShowRationale(permission: String): Boolean =
        activity?.shouldShowRequestPermissionRationale(permission) == true

    /** Shows the system dialog and suspends for the result; null when no Activity is attached. */
    suspend fun request(permissions: Array<String>): Map<String, Boolean>? {
        val launcher = launcher ?: return null
        pending?.cancel()
        val deferred = CompletableDeferred<Map<String, Boolean>>()
        pending = deferred
        launcher.launch(permissions)
        return deferred.await()
    }
}
