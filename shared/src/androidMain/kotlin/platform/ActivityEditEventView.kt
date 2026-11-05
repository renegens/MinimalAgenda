package platform

import domain.model.AgendaEvent

class ActivityEditEventView: EventEditViewHandler {
    override suspend fun openAddEvent(onSave: () -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun openEdit(agendaEvent: AgendaEvent, onSave: () -> Unit) {
        TODO("Not yet implemented")
    }
}