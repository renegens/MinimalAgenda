package repository

import data.local.HiddenEventsLocalDataSource

class HiddenEventsRepositoryImpl(private val hiddenEventsLocalDataSource: HiddenEventsLocalDataSource) :
    HiddenEventsRepository {

    override suspend fun getEvents(): List<String> {
        //hiddenEventsLocalDataSource.getEvents()
        TODO("Not yet implemented")
    }

    override suspend fun hideEvent(eventId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unhideEvent(eventId: String) {
        TODO("Not yet implemented")
    }
}