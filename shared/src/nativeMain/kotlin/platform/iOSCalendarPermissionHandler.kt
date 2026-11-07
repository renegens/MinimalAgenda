package platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.EventKit.EKAuthorizationStatusDenied
import platform.EventKit.EKAuthorizationStatusFullAccess
import platform.EventKit.EKAuthorizationStatusNotDetermined
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore
import platform.UIKit.UIDevice

@Suppress("ClassName")
class iOSCalendarPermissionHandler(eventKit: EventKit) : CalendarPermissionHandler {

    private val iOS_17 = 17
    private val eventStore = eventKit.eventStore

    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State {
        return suspendCancellableCoroutine { continuation ->
            val system = UIDevice.currentDevice.systemVersion.substringBefore('.').toIntOrNull() ?: 0
            if (system >= iOS_17) {
                eventStore.requestFullAccessToEventsWithCompletion { granted, error ->
                    if (granted) {
                        continuation.resumeWith(Result.success(CalendarPermissionHandler.State.FullAccess))
                    } else {
                        continuation.resumeWith(Result.success(CalendarPermissionHandler.State.Denied))
                    }
                }
            } else {
                eventStore.requestAccessToEntityType(EKEntityType.EKEntityTypeEvent) { granted, error ->
                    if (granted) {
                        continuation.resumeWith(Result.success(CalendarPermissionHandler.State.FullAccess))
                    } else {
                        continuation.resumeWith(Result.success(CalendarPermissionHandler.State.Denied))
                    }
                }
            }
        }
    }

    override fun getPermissionState(): CalendarPermissionHandler.State {
        return when (EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)) {
            EKAuthorizationStatusFullAccess -> CalendarPermissionHandler.State.FullAccess
            EKAuthorizationStatusDenied -> CalendarPermissionHandler.State.Denied
            EKAuthorizationStatusNotDetermined -> CalendarPermissionHandler.State.NotDetermined
            else -> CalendarPermissionHandler.State.Denied
        }
    }
}