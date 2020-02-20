package com.pointlessapps.mobileusos.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pointlessapps.mobileusos.models.University

@Dao
interface UniversityDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insert(vararg universities: University)

	@Update
	fun update(vararg universities: University)

	@Delete
	fun delete(vararg universities: University)

	@Query("SELECT * FROM table_universities ORDER BY location AND name ASC")
	fun getAll(): LiveData<List<University>>

	@Query("SELECT * FROM table_universities WHERE name == :name ORDER BY location ASC")
	fun getByName(name: String): LiveData<List<University>>

	@Query("SELECT * FROM table_universities WHERE location == :location ORDER BY name ASC")
	fun getByLocation(location: String): LiveData<List<University>>
}