package domain.use_case

import domain.model.Agenda
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import repository.CalendarRepository

class CalendarWithEventsUseCase(
    private val calendarRepository: CalendarRepository,
    private val calendarGenerator: CalendarGenerator
) {

    private val KAIZEN_ID = 7L

    suspend operator fun invoke(): Agenda {
        return withContext(Dispatchers.IO) {

            val emptyCalendar = calendarGenerator.run()
            val calendarEvents = calendarRepository.getEventsAndroid(KAIZEN_ID)

            emptyCalendar.days.map { day ->
                val eventsForDay = calendarEvents.filter { it.epochDay == day.epochDay }
                day.events = eventsForDay

            }
            return@withContext emptyCalendar
        }
    }
}