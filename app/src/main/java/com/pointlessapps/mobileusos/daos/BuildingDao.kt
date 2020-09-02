package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.Building

@Dao
interface BuildingDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg building: Building)

	@Query("SELECT * FROM table_buildings WHERE id = :buildingId")
	suspend fun getById(buildingId: String): Building?
}
