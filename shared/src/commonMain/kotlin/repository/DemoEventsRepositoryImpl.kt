package repository

import data.local.HiddenEventEntity
import data.local.HiddenEventsLocalDataSource
import domain.model.AgendaEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DemoEventsRepositoryImpl() :
    DemoEventsRepository {

    override fun getEvents(events: List<AgendaEvent>) {
        TODO("Not yet implemented")
    }

    override fun addEvent(event: AgendaEvent) {
        TODO("Not yet implemented")
    }
}