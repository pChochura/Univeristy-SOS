package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.University

@Dao
interface UniversityDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg universities: University)

	@Update
	suspend fun update(vararg universities: University)

	@Delete
	suspend fun delete(vararg universities: University)

	@Query("SELECT * FROM table_universities ORDER BY location AND name ASC")
	suspend fun getAll(): List<University>
}