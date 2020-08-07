package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersBuilding {

	@TypeConverter
	fun fromRooms(buildingRooms: List<BuildingRoom>?): String = Gson().toJson(buildingRooms)

	@TypeConverter
	fun toRooms(json: String): List<BuildingRoom>? = Gson().fromJson(json)
}
