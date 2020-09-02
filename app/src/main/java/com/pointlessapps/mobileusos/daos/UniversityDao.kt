package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.University

@Dao
interface UniversityDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg universities: University)

	@Query("SELECT * FROM table_universities")
	suspend fun getAll(): List<University>
}
