package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.CourseEvent
import java.util.*

@Dao
interface TimetableDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courseEvents: CourseEvent)

	@Update
	suspend fun update(vararg courseEvents: CourseEvent)

	@Delete
	suspend fun delete(vararg courseEvents: CourseEvent)

	@Query("""
		SELECT * FROM table_course_events
		WHERE user_id == :userId 
		AND start_time >= :startTime 
		AND datetime('now', 'now', '+' || :numberOfDays || ' day')
	""")
	suspend fun getByUser(userId: String?, startTime: Calendar, numberOfDays: Int = 7): List<CourseEvent>
}