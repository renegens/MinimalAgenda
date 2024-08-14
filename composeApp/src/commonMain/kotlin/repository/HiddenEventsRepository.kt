package repository

interface HiddenEventsRepository {
    suspend fun getEvents(): List<String>
    suspend fun hideEvent(eventId: String)
    suspend fun unhideEvent(eventId: String)
}