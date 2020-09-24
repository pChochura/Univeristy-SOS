package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_rooms")
@Keep
data class BuildingRoom(
	@SerializedName("building")
	var building: Building? = null,
	@SerializedName("attributes")
	var attributes: List<Attribute>? = null,
	@SerializedName("capacity")
	var capacity: Int? = null,
	@SerializedName("number")
	var number: String? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) : Comparable<BuildingRoom> {

	override fun compareTo(other: BuildingRoom) = compareValuesBy(this, other, { it.number })

	@Keep
	data class Attribute(
		@SerializedName("id")
		var id: String,
		@SerializedName("description")
		var description: Name,
		@SerializedName("count")
		var count: Int?
	)
}
