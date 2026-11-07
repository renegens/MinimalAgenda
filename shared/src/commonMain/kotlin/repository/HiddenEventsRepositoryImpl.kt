package repository

import data.local.HiddenEventEntity
import data.local.HiddenEventsLocalDataSource
import domain.model.AgendaEvent
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class HiddenEventsRepositoryImpl(private val hiddenEventsLocalDataSource: HiddenEventsLocalDataSource) :
    HiddenEventsRepository {

    override suspend fun getEvents(): List<HiddenEventEntity> =
        HiddenEventsJson.decode(hiddenEventsLocalDataSource.getEvents())

    override suspend fun hideEvent(agendaEvent: AgendaEvent) = update(agendaEvent) { events, event -> events + event }
    override suspend fun unhiddenEvent(agendaEvent: AgendaEvent) = update(agendaEvent) { events, event -> events - event }
    override suspend fun toggleEventVisibility(agendaEvent: AgendaEvent) = update(agendaEvent) { events, event ->
        if (event in events) events - event else events + event
    }

    private suspend fun update(event: AgendaEvent, transform: (Set<HiddenEventEntity>, HiddenEventEntity) -> Set<HiddenEventEntity>) {
        hiddenEventsLocalDataSource.updateEvents { stored ->
            HiddenEventsJson.encode(transform(HiddenEventsJson.decode(stored).toSet(), event.toHiddenEventEntity()).toList())
        }
    }
}

internal object HiddenEventsJson {
    private val json = Json { ignoreUnknownKeys = true }
    fun decode(value: String): List<HiddenEventEntity> = try {
        if (value.isBlank()) emptyList() else json.decodeFromString<List<HiddenEventEntity>>(value)
    } catch (_: SerializationException) {
        emptyList()
    }
    fun encode(value: List<HiddenEventEntity>): String = json.encodeToString(value)
}
