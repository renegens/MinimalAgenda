package repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Instances
import data.calendar.CalendarEntity
import data.calendar.CalendarMath
import data.calendar.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import platform.uses24HourClock
import java.util.Calendar

class AndroidCalendarRepository(private val context: Context) : CalendarRepository {

    override suspend fun getCalendars(): List<CalendarEntity> = withContext(Dispatchers.IO) {
        val calendars = mutableListOf<CalendarEntity>()
        val projection = arrayOf(
            Calendars._ID,
            Calendars.CALENDAR_DISPLAY_NAME,
            Calendars.ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE,
        )
        try {
            context.contentResolver.query(
                Calendars.CONTENT_URI, projection, "${Calendars.VISIBLE} = 1", null, null
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    calendars.add(
                        CalendarEntity(
                            id = cursor.getLong(0).toString(),
                            name = cursor.getString(1).orEmpty(),
                            accountName = cursor.getString(2).orEmpty(),
                            accountType = cursor.getString(3).orEmpty(),
                        )
                    )
                }
            }
        } catch (_: SecurityException) {
            return@withContext emptyList()
        }
        calendars
    }

    override suspend fun createLocalCalendar(): Boolean = withContext(Dispatchers.IO) {
        try {
            val existing = context.contentResolver.query(
                Calendars.CONTENT_URI,
                arrayOf(Calendars._ID),
                "${Calendars.ACCOUNT_NAME} = ? AND ${Calendars.ACCOUNT_TYPE} = ?",
                arrayOf(LOCAL_ACCOUNT_NAME, CalendarContract.ACCOUNT_TYPE_LOCAL),
                null
            )?.use { if (it.moveToFirst()) it.getLong(0) else null }

            if (existing != null) {
                // Already there (probably hidden); make it visible again instead of adding a duplicate.
                val values = ContentValues().apply {
                    put(Calendars.VISIBLE, 1)
                    put(Calendars.SYNC_EVENTS, 1)
                }
                val uri = asSyncAdapter(ContentUris.withAppendedId(Calendars.CONTENT_URI, existing))
                return@withContext context.contentResolver.update(uri, values, null, null) > 0
            }

            val values = ContentValues().apply {
                put(Calendars.ACCOUNT_NAME, LOCAL_ACCOUNT_NAME)
                put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                put(Calendars.NAME, LOCAL_ACCOUNT_NAME)
                put(Calendars.CALENDAR_DISPLAY_NAME, LOCAL_ACCOUNT_NAME)
                put(Calendars.CALENDAR_COLOR, Color.parseColor("#E65100"))
                put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER)
                put(Calendars.OWNER_ACCOUNT, LOCAL_ACCOUNT_NAME)
                put(Calendars.VISIBLE, 1)
                put(Calendars.SYNC_EVENTS, 1)
                put(Calendars.CALENDAR_TIME_ZONE, java.util.TimeZone.getDefault().id)
            }
            context.contentResolver.insert(asSyncAdapter(Calendars.CONTENT_URI), values) != null
        } catch (_: SecurityException) {
            false
        } catch (_: RuntimeException) {
            false
        }
    }

    private fun asSyncAdapter(uri: Uri): Uri = uri.buildUpon()
        .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
        .appendQueryParameter(Calendars.ACCOUNT_NAME, LOCAL_ACCOUNT_NAME)
        .appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        .build()

    /** Same window as the iOS repository: [now - months, now + months]. */
    override suspend fun getEvents(durationInMonths: Int): List<EventEntity> {
        val start = Calendar.getInstance().apply { add(Calendar.MONTH, -durationInMonths) }.timeInMillis
        val end = Calendar.getInstance().apply { add(Calendar.MONTH, durationInMonths) }.timeInMillis
        return queryInstances(start, end, selection = null, selectionArgs = null)
    }

    override suspend fun getEvent(id: String): EventEntity? {
        val instance = CalendarMath.parseInstanceId(id) ?: return null
        return queryInstances(
            beginMillis = instance.beginMillis - 1,
            endMillis = instance.beginMillis + 1,
            selection = "${Instances.EVENT_ID} = ?",
            selectionArgs = arrayOf(instance.eventId.toString()),
        ).firstOrNull { it.id == id }
    }

    private suspend fun queryInstances(
        beginMillis: Long,
        endMillis: Long,
        selection: String?,
        selectionArgs: Array<String>?,
    ): List<EventEntity> = withContext(Dispatchers.IO) {
        val uri = Instances.CONTENT_URI.buildUpon().also {
            ContentUris.appendId(it, beginMillis)
            ContentUris.appendId(it, endMillis)
        }.build()
        val visibleOnly = "${Instances.VISIBLE} = 1"
        val fullSelection = if (selection == null) visibleOnly else "($visibleOnly) AND ($selection)"
        val zone = TimeZone.currentSystemDefault()
        val uses24HourClock = uses24HourClock()
        val events = mutableListOf<EventEntity>()

        try {
            val cursor: Cursor? = context.contentResolver.query(
                uri, INSTANCE_PROJECTION, fullSelection, selectionArgs, "${Instances.BEGIN} ASC"
            )
            cursor?.use {
                while (it.moveToNext()) {
                    val eventId = it.getLong(0)
                    val begin = it.getLong(1)
                    val end = it.getLong(2)
                    val allDay = it.getInt(5) != 0
                    val times = CalendarMath.resolveTimes(begin, end, allDay, zone)

                    events.add(
                        EventEntity(
                            id = CalendarMath.buildInstanceId(eventId, begin),
                            title = it.getString(3),
                            location = it.getString(4),
                            startTime = times.startSeconds,
                            endTime = times.endSeconds,
                            allDay = allDay,
                            isRecurring = CalendarMath.isRecurring(it.getString(6)),
                            epochDay = times.epochDay,
                            startLocalDateTime = times.startLocalDateTime,
                            endLocalDateTime = times.endLocalDateTime,
                            isMultiDay = times.isMultiDay,
                            uses24HourClock = uses24HourClock,
                        )
                    )
                }
            }
        } catch (_: SecurityException) {
            return@withContext emptyList()
        }
        events
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

    private companion object {
        const val LOCAL_ACCOUNT_NAME = "Minimal Agenda"

        val INSTANCE_PROJECTION = arrayOf(
            Instances.EVENT_ID,        // 0
            Instances.BEGIN,           // 1
            Instances.END,             // 2
            Instances.TITLE,           // 3
            Instances.EVENT_LOCATION,  // 4
            Instances.ALL_DAY,         // 5
            Instances.RRULE,           // 6
            Instances.CALENDAR_COLOR,  // 7
        )
    }
}
