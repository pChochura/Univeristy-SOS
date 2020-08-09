package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.CalendarEvent
import java.util.*

@Dao
interface CalendarDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg calendar: CalendarEvent)

	@Update
	suspend fun update(vararg calendar: CalendarEvent)

	@Delete
	suspend fun delete(vararg calendar: CalendarEvent)

	@Query("SELECT * FROM table_calendar_events WHERE facId IN(:faculties) AND start_date >= :startDate AND end_date <= :endDate")
	suspend fun getByFaculties(
		faculties: List<String>,
		startDate: Date,
		endDate: Date
	): List<CalendarEvent>
}
