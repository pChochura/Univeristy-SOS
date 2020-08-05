package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersRoom {

	@TypeConverter
	fun fromRoom(buildingRoom: BuildingRoom?): String = Gson().toJson(buildingRoom)

	@TypeConverter
	fun toRoom(json: String): BuildingRoom? = Gson().fromJson(json)

	@TypeConverter
	fun fromBuilding(room: Building?): String = Gson().toJson(room)

	@TypeConverter
	fun toBuilding(json: String): Building? = Gson().fromJson(json)

	@TypeConverter
	fun fromAttributes(attributes: List<BuildingRoom.Attribute>?): String =
		Gson().toJson(attributes)

	@TypeConverter
	fun toAttributes(json: String): List<BuildingRoom.Attribute>? = Gson().fromJson(json)

	@TypeConverter
	fun fromLocation(attributes: Building.Location?): String =
		Gson().toJson(attributes)

	@TypeConverter
	fun toLocation(json: String): Building.Location? = Gson().fromJson(json)
}
