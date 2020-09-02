package com.pointlessapps.mobileusos.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.mobileusos.models.BuildingRoom

@Dao
interface RoomDao {

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(vararg buildingRooms: BuildingRoom)

	@Query("SELECT * FROM table_rooms WHERE id = :roomId")
	suspend fun getById(roomId: String): BuildingRoom?
}
