package platform

import domain.model.AgendaEvent

interface EventEditViewHandler {
    suspend fun openAddEvent(onSave: () -> Unit)
    suspend fun openEdit(agendaEvent: AgendaEvent, onSave: () -> Unit)
}