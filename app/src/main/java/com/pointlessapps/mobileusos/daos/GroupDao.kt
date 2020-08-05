package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Course

@Dao
interface GroupDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courses: Course)

	@Update
	suspend fun update(vararg courses: Course)

	@Delete
	suspend fun delete(vararg courses: Course)

	@Query("SELECT * FROM table_groups")
	suspend fun getAll(): List<Course>

	@Query("SELECT * FROM table_groups WHERE course_unit_id = :courseUnitId AND group_number = :groupNumber")
	suspend fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int): Course
}
