package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.User

@Dao
interface FacultyDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg faculty: User.Faculty)

	@Query("SELECT * FROM table_faculties LIMIT 1")
	suspend fun getPrimaryFaculty(): User.Faculty?
}
