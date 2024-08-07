package domain.use_case

import domain.model.AgendaDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import repository.CalendarRepository

class CalendarWithEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val calendarGenerator: CalendarGenerator
) {

    private val KAIZEN_ID = 7L

    suspend operator fun invoke(): List<AgendaDay> {
        return withContext(Dispatchers.IO) {

            val emptyCalendar = calendarGenerator()
            val calendarEvents = calendarRepository.getEventsAndroid(KAIZEN_ID)

            emptyCalendar.map { day ->
                val eventsForDay = calendarEvents.filter { it.epochDay == day.epochDay }
                day.events = eventsForDay

            }
            return@withContext emptyCalendar
        }
    }
}