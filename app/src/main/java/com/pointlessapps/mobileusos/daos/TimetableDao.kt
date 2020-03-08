package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.CourseEvent

@Dao
interface TimetableDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courseEvents: CourseEvent)

	@Update
	suspend fun update(vararg courseEvents: CourseEvent)

	@Delete
	suspend fun delete(vararg courseEvents: CourseEvent)

	@Query("SELECT * FROM table_course_events WHERE start_time BETWEEN :startTime AND :endTime")
	suspend fun getForDays(startTime: Long, endTime: Long): List<CourseEvent>
}