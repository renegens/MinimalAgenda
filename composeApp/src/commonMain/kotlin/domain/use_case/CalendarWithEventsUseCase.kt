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
    private val EMULATOR_HOME_CALENDAR = "8D5E6554-E85B-47F4-824B-9A3B63626D38"

    suspend operator fun invoke(): List<AgendaDay> {
        return withContext(Dispatchers.IO) {

            val emptyCalendar = calendarGenerator()
            val calendars = calendarRepository.getCalendars()
            println(calendars)
            //val calendarEvents = calendarRepository.getEvents(KAIZEN_ID.toString())
            val calendarEvents = calendarRepository.getEvents(EMULATOR_HOME_CALENDAR)
            emptyCalendar.map { day ->
                val eventsForDay = calendarEvents.filter { it.epochDay == day.epochDay }
                day.events = eventsForDay
            }
            return@withContext emptyCalendar
        }
    }
}