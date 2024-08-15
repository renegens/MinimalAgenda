package repository

import data.local.HiddenEventEntity
import domain.model.AgendaEvent

interface HiddenEventsRepository {
    suspend fun getEvents(): List<HiddenEventEntity>
    suspend fun hideEvent(agendaEvent: AgendaEvent)
    suspend fun unhiddenEvent(agendaEvent: AgendaEvent)
    suspend fun toggleEventVisibility(agendaEvent: AgendaEvent)
}