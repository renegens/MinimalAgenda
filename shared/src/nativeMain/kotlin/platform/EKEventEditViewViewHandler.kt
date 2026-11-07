package platform

import domain.model.AgendaEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.EventKit.EKEvent
import platform.EventKitUI.EKEventEditViewAction
import platform.EventKitUI.EKEventEditViewActionDeleted
import platform.EventKitUI.EKEventEditViewActionSaved
import platform.EventKitUI.EKEventEditViewController
import platform.EventKitUI.EKEventEditViewDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.darwin.NSObject

class EKEventEditViewViewHandler(eventKit: EventKit) : EventEditViewHandler {

    private val eventStore = eventKit.eventStore
    // EventKitUI retains its delegate weakly; keep it alive until the editor finishes.
    private var activeDelegate: EKEventEditViewDelegate? = null

    override suspend fun openAddEvent(onSave: () -> Unit) {
        withContext(Dispatchers.Main) {
            val event = EKEvent.eventWithEventStore(eventStore)
            val eventEditVC = EKEventEditViewController()
            eventEditVC.event = event
            eventEditVC.eventStore = eventStore
            eventEditVC.navigationBar.tintColor = UIColor.brownColor

            eventEditVC.editViewDelegate = EKEventEditViewDelegate(onSave) { activeDelegate = null }.also { activeDelegate = it }

            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController

            rootVC?.presentViewController(eventEditVC, animated = true, completion = {
            })

        }
    }

    override suspend fun openEdit(agendaEvent: AgendaEvent, onSave: () -> Unit): Unit = withContext(Dispatchers.Main) {
            val event = eventStore.eventWithIdentifier(agendaEvent.id)
            if (event != null) {
                val eventEditVC = EKEventEditViewController()
                eventEditVC.event = event
                eventEditVC.eventStore = eventStore
                eventEditVC.editViewDelegate = EKEventEditViewDelegate(onSave) { activeDelegate = null }.also { activeDelegate = it }

                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController

                rootVC?.presentViewController(eventEditVC, animated = true, null)
            } else {
                // Handle the case where the event is not found
                //println("Event not found")
            }
    }

    private class EKEventEditViewDelegate(private val onSave: () -> Unit, private val onFinished: () -> Unit) : NSObject(),
        EKEventEditViewDelegateProtocol {

        override fun eventEditViewController(
            controller: EKEventEditViewController,
            didCompleteWithAction: EKEventEditViewAction
        ) {
            onFinished()
            if (didCompleteWithAction == EKEventEditViewActionSaved || didCompleteWithAction == EKEventEditViewActionDeleted){
                controller.dismissViewControllerAnimated(true, null)
                onSave.invoke()
            } else {
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }

}
