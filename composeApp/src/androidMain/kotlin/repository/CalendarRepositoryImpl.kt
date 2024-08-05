package repository

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import data.CalendarData
import data.EventData
import extensions.millisToEpochDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class CalendarRepositoryImpl(private val context: Context) : CalendarRepository {

    override suspend fun getCalendarsAndroid(): List<CalendarData> {
        return withContext(Dispatchers.IO) {
            val calendars = mutableListOf<CalendarData>()
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

                    calendars.add(CalendarData(id, name, accountName, accountType))
                }
            }

            return@withContext calendars
        }

    }

    override suspend fun getEventsAndroid(calendarId: Long): List<EventData> {
        return withContext(Dispatchers.IO) {
            val events = mutableListOf<EventData>()
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
                calendarId.toString(),
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
                        EventData(
                            id,
                            title,
                            description,
                            startTime,
                            endTime,
                            epochDay = endTime.millisToEpochDay()
                        )
                    )
                }
            }

            return@withContext events
        }

    }

    override fun getCalendarsiOS(): List<CalendarData> {
        return emptyList()
    }
}