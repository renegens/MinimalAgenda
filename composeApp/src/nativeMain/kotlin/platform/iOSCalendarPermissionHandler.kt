package platform

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.EventKit.EKAuthorizationStatusDenied
import platform.EventKit.EKAuthorizationStatusFullAccess
import platform.EventKit.EKAuthorizationStatusNotDetermined
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore

@Suppress("ClassName")
class iOSCalendarPermissionHandler(eventKit: EventKit) : CalendarPermissionHandler {

    private val eventStore = eventKit.eventStore

    override suspend fun requestCalendarPermission(): CalendarPermissionHandler.State {
        return suspendCancellableCoroutine { continuation ->
            eventStore.requestFullAccessToEventsWithCompletion { granted, error ->
                if (granted) {
                    continuation.resumeWith(Result.success(CalendarPermissionHandler.State.FullAccess))
                } else {
                    continuation.resumeWith(Result.success(CalendarPermissionHandler.State.Denied))
                }
            }
        }
    }

    override fun getPermissionState(): CalendarPermissionHandler.State {
        println(EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent))
        return when (EKEventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)) {
            EKAuthorizationStatusFullAccess -> CalendarPermissionHandler.State.FullAccess
            EKAuthorizationStatusDenied -> CalendarPermissionHandler.State.Denied
            EKAuthorizationStatusNotDetermined -> CalendarPermissionHandler.State.NotDetermined
            else -> CalendarPermissionHandler.State.Denied
        }
    }
}