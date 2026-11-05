package platform

import domain.model.AgendaEvent
import presentation.screens.add_event.EventEditorRequest
import presentation.screens.add_event.EventEditorRequests

/** Opens the shared editor (a navigation route in `App`) for adding or editing an event. */
class ActivityEditEventView(private val editorRequests: EventEditorRequests) : EventEditViewHandler {
    override suspend fun openAddEvent(onSave: () -> Unit) {
        editorRequests.open(EventEditorRequest.Add, onSave)
    }

    override suspend fun openEdit(agendaEvent: AgendaEvent, onSave: () -> Unit) {
        editorRequests.open(EventEditorRequest.Edit(agendaEvent), onSave)
    }
}
