package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.CalendarEvent

@Dao
interface CalendarDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg calendar: CalendarEvent)

	@Query("SELECT * FROM table_calendar_events WHERE facId IN(:faculties) AND start_date BETWEEN :startDate AND :endDate")
	suspend fun getByFaculties(
		faculties: List<String>,
		startDate: Long,
		endDate: Long
	): List<CalendarEvent>
}
