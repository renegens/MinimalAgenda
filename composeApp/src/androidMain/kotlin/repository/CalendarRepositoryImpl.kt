package repository

import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import data.CalendarData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    override fun getCalendarsiOS(): List<CalendarData> {
        return emptyList()
    }
}