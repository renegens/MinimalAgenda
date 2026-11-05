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
import platform.EventKitUI.EKEventViewAction
import platform.EventKitUI.EKEventViewController
import platform.EventKitUI.EKEventViewDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationController
import platform.darwin.NSObject

class EKEventEditViewViewHandler(eventKit: EventKit) : EventEditViewHandler {

    private val eventStore = eventKit.eventStore

    override suspend fun openAddEvent(onSave: () -> Unit) {
        withContext(Dispatchers.Main) {
            val event = EKEvent.eventWithEventStore(eventStore)
            val eventEditVC = EKEventEditViewController()
            eventEditVC.event = event
            eventEditVC.eventStore = eventStore
            eventEditVC.navigationBar.tintColor = UIColor.brownColor

            eventEditVC.editViewDelegate = EKEventEditViewDelegate(onSave)

            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController

            rootVC?.presentViewController(eventEditVC, animated = true, completion = {
            })

        }
    }

    override suspend fun openEdit(agendaEvent: AgendaEvent, onSave: () -> Unit) {
            val event = eventStore.eventWithIdentifier(agendaEvent.id)
            if (event != null) {
                val eventEditVC = EKEventEditViewController()
                eventEditVC.event = event
                eventEditVC.eventStore = eventStore
                eventEditVC.editViewDelegate = EKEventEditViewDelegate(onSave)

                val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController

                rootVC?.presentViewController(eventEditVC, animated = true, null)
            } else {
                // Handle the case where the event is not found
                //println("Event not found")
            }
    }

    private fun openView(agendaEvent: AgendaEvent, onSave: () -> Unit){
        val event = eventStore.eventWithIdentifier(agendaEvent.id)

        val eventViewController = EKEventViewController()
        eventViewController.event = event
        eventViewController.allowsEditing = true
        eventViewController.delegate = EKEventViewDelegate(onSave)

       // Present the EKEventViewController
        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        val navigationController =
            UINavigationController(rootViewController = eventViewController)
        rootVC?.presentViewController(
            navigationController,
            animated = true,
            completion = null
        )
    }

    private class EKEventEditViewDelegate(private val onSave: () -> Unit) : NSObject(),
        EKEventEditViewDelegateProtocol {

        override fun eventEditViewController(
            controller: EKEventEditViewController,
            didCompleteWithAction: EKEventEditViewAction
        ) {
            if (didCompleteWithAction == EKEventEditViewActionSaved || didCompleteWithAction == EKEventEditViewActionDeleted){
                controller.dismissViewControllerAnimated(true, null)
                onSave.invoke()
            } else {
                controller.dismissViewControllerAnimated(true, null)
            }
        }
    }

    private class EKEventViewDelegate(private val onSave: () -> Unit) : NSObject(),
        EKEventViewDelegateProtocol {

        override fun eventViewController(
            controller: EKEventViewController,
            didCompleteWithAction: EKEventViewAction
        ) {
            controller.dismissViewControllerAnimated(true, { onSave.invoke() })
        }
    }

}