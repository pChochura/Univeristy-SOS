package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.CourseEvent

@Dao
interface TimetableDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courseEvents: CourseEvent)

	@Query("DELETE FROM table_course_events WHERE course_id == :courseId AND unit_id == :unitId AND start_time == :startTime")
	suspend fun deleteByCompositeId(courseId: String, unitId: String, startTime: Long)

	@Query(
		"""
		SELECT * FROM table_course_events
			WHERE (start_time BETWEEN :startTime AND :endTime)
				OR (
					frequency IS NOT NULL
					AND :startTime < end_time_repeating
					AND (((:startTime - start_time) / frequency + 1) * frequency + start_time) BETWEEN :startTime AND :endTime
				)
			ORDER BY start_time DESC
		"""
	)
	suspend fun getForDays(startTime: Long, endTime: Long): List<CourseEvent>
}
