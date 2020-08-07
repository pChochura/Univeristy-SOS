package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.Building

@Dao
interface BuildingDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg building: Building)

	@Update
	suspend fun update(vararg building: Building)

	@Delete
	suspend fun delete(vararg building: Building)

	@Query("SELECT * FROM table_buildings WHERE id = :buildingId")
	suspend fun getById(buildingId: String): Building?
}
