package repository

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import data.calendar.CalendarEntity
import data.calendar.EventEntity
import extensions.millisToEpochDay
import extensions.today
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.UUID
import kotlin.random.Random

class AndroidCalendarRepository(private val context: Context) : CalendarRepository {

    override suspend fun getCalendars(): List<CalendarEntity> {
        return withContext(Dispatchers.IO) {
            val calendars = mutableListOf<CalendarEntity>()
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE
            )
            val uri = CalendarContract.Calendars.CONTENT_URI
            val selection = null
            val selectionArgs = null
            val sortOrder = null

            val cursor: Cursor? = context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(CalendarContract.Calendars._ID)
                val nameIndex = it.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                val accountNameIndex = it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME)
                val accountTypeIndex = it.getColumnIndex(CalendarContract.Calendars.ACCOUNT_TYPE)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val name = it.getString(nameIndex)
                    val accountName = it.getString(accountNameIndex)
                    val accountType = it.getString(accountTypeIndex)

                    calendars.add(CalendarEntity(id.toString(), name, accountName, accountType))
                }
            }

            return@withContext calendars
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun dateFormatter(milliseconds: String): String {
        return SimpleDateFormat("HH : mm").format(Date(milliseconds.toLong())).toString()
    }

    override suspend fun getEvents(durationInMonths: Int): List<EventEntity> {
        val list = emptyList<EventEntity>().toMutableList()

        for (i in 1..8000) {
            val timestamp = getRandomTimestampWithinTwoYears()
            list.add(
                EventEntity(
                    id = UUID.randomUUID().toString(),
                    title = UUID.randomUUID().toString(),
                    location = "",
                    startTime = timestamp,
                    endTime = timestamp + 6000,
                    allDay = false,
                    epochDay = calculateEpochDay(timestamp, TimeZone.currentSystemDefault()),
                    isRecurring = false,
                    startLocalDateTime = Instant.fromEpochSeconds(timestamp).toLocalDateTime(TimeZone.currentSystemDefault()),
                    endLocalDateTime = Instant.fromEpochSeconds(timestamp + 6000).toLocalDateTime(TimeZone.currentSystemDefault()),
                    uses24HourClock = false,
                    isMultiDay = false,
                    )
            )
        }
        return list

        return withContext(Dispatchers.IO) {
            val events = mutableListOf<EventEntity>()
            val projection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND
            )
            val uri = CalendarContract.Events.CONTENT_URI

            val startOfRange: Calendar = Calendar.getInstance().apply {
                add(Calendar.YEAR, -2)
            }

            val endOfRange: Calendar = Calendar.getInstance().apply {
                add(Calendar.YEAR, 2)
            }

            val selection =
                "${CalendarContract.Events.CALENDAR_ID} = ? AND ${CalendarContract.Events.DTSTART} >= ? AND ${CalendarContract.Events.DTSTART} <= ?"
            val selectionArgs = arrayOf(
                "calendarId",
                startOfRange.timeInMillis.toString(),
                endOfRange.timeInMillis.toString()
            )
            val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

            val cursor: Cursor? = context.contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )

            cursor?.use {
                val idIndex = it.getColumnIndex(CalendarContract.Events._ID)
                val titleIndex = it.getColumnIndex(CalendarContract.Events.TITLE)
                val descriptionIndex = it.getColumnIndex(CalendarContract.Events.DESCRIPTION)
                val startTimeIndex = it.getColumnIndex(CalendarContract.Events.DTSTART)
                val endTimeIndex = it.getColumnIndex(CalendarContract.Events.DTEND)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val title = it.getString(titleIndex)
                    val description = it.getString(descriptionIndex)
                    val startTime = it.getLong(startTimeIndex)
                    val endTime = it.getLong(endTimeIndex)

                    events.add(
                        EventEntity(
                            id = id.toString(),
                            title = title,
                            location = "",
                            startTime = startTime,
                            endTime = startTime + 6000,
                            allDay = false,
                            isRecurring = false,
                            epochDay = endTime.millisToEpochDay(),
                            startLocalDateTime = Instant.fromEpochSeconds(startTime).toLocalDateTime(TimeZone.currentSystemDefault()),
                            endLocalDateTime = Instant.fromEpochSeconds(startTime + 6000).toLocalDateTime(TimeZone.currentSystemDefault()),
                            isMultiDay = false,
                            uses24HourClock = false
                        )
                    )
                }
            }

            return@withContext events
        }
    }

    override suspend fun getEvent(id: String): EventEntity? {
        TODO("Not yet implemented")
    }

    override suspend fun addEvent(
        title: String,
        location: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        allDay: Boolean
    ): Boolean {
        return false
    }

    private fun calculateEpochDay(starTime: Long?, timeZone: TimeZone): Int {
        return Instant.fromEpochSeconds(starTime ?: 0).toLocalDateTime(timeZone).date.toEpochDays()
    }
}

fun getRandomTimestampWithinTwoYears(): Long {
    // Current time
    val startYear = LocalDate(year = today.year - 2, monthNumber = 1, dayOfMonth = 1)
    val endDate = LocalDate(year = startYear.year + 4, monthNumber = 12, dayOfMonth = 31)


    // Convert start and end times to epoch milliseconds for easier random calculation
    val startMillis = startYear.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds
    val endMillis = endDate.atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds

    // Generate a random time between startMillis and endMillis
    val randomMillis = Random.nextLong(startMillis, endMillis)

    // Convert the random milliseconds back to an Instant
    return randomMillis
}
