package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.CourseEvent

@Dao
interface TimetableDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courseEvents: CourseEvent)

	@Query("SELECT * FROM table_course_events WHERE start_time BETWEEN :startTime AND :endTime ORDER BY start_time DESC")
	suspend fun getForDays(startTime: Long, endTime: Long): List<CourseEvent>
}
