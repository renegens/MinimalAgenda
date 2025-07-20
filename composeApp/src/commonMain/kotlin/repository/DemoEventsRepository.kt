package repository

import domain.model.AgendaEvent

interface DemoEventsRepository {
    fun getEvents(events: List<AgendaEvent>)
    fun addEvent(event: AgendaEvent)
}