package com.pointlessapps.mobileusos.daos

import androidx.room.*
import com.pointlessapps.mobileusos.models.BuildingRoom

@Dao
interface RoomDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg buildingRooms: BuildingRoom)

	@Update
	suspend fun update(vararg buildingRooms: BuildingRoom)

	@Delete
	suspend fun delete(vararg buildingRooms: BuildingRoom)

	@Query("SELECT * FROM table_rooms WHERE id = :roomId")
	suspend fun getById(roomId: String): BuildingRoom?
}
