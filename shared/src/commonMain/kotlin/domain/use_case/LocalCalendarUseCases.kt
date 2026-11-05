package domain.use_case

import repository.CalendarRepository

/** True when the device has at least one visible calendar. */
class HasCalendarsUseCase(private val calendarRepository: CalendarRepository) {
    suspend operator fun invoke(): Boolean = calendarRepository.getCalendars().isNotEmpty()
}

/** Creates a calendar that stays on this device. Returns true on success. */
class CreateLocalCalendarUseCase(private val calendarRepository: CalendarRepository) {
    suspend operator fun invoke(): Boolean = calendarRepository.createLocalCalendar()
}
