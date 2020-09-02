package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Course

@Dao
interface GroupDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg courses: Course)

	@Query("SELECT * FROM table_groups")
	suspend fun getAll(): List<Course>

	@Query("SELECT * FROM table_groups WHERE course_unit_id = :courseUnitId AND group_number = :groupNumber")
	suspend fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int): Course
}
