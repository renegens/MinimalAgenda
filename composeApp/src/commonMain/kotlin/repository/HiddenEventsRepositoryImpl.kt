package repository

import data.local.HiddenEventEntity
import data.local.HiddenEventsLocalDataSource
import domain.model.AgendaEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class HiddenEventsRepositoryImpl(private val hiddenEventsLocalDataSource: HiddenEventsLocalDataSource) :
    HiddenEventsRepository {

    override suspend fun getEvents(): List<HiddenEventEntity> {
        val json = hiddenEventsLocalDataSource.getEvents()
        if (json.isEmpty()) return emptyList()
        return Json.decodeFromString<List<HiddenEventEntity>>(json)
    }

    override suspend fun hideEvent(agendaEvent: AgendaEvent) {
        val list = getEvents().toMutableList()
        list.add(agendaEvent.toHiddenEventEntity())
        val listToSave = Json.encodeToString(list)
        hiddenEventsLocalDataSource.saveEvents(listToSave)
    }

    override suspend fun unhiddenEvent(agendaEvent: AgendaEvent) {
        val list = getEvents().toMutableList()
        list.remove(agendaEvent.toHiddenEventEntity())
        val listToSave = Json.encodeToString(list)
        hiddenEventsLocalDataSource.saveEvents(listToSave)
    }

    override suspend fun toggleEventVisibility(agendaEvent: AgendaEvent) {
        val list = getEvents().toMutableList()
        val hiddenEventEntity = agendaEvent.toHiddenEventEntity()
        if (list.contains(hiddenEventEntity)) {
            list.remove(hiddenEventEntity)
        } else {
            list.add(hiddenEventEntity)
        }
        val listToSave = Json.encodeToString(list)
        hiddenEventsLocalDataSource.saveEvents(listToSave)
    }
}