package com.pointlessapps.mobileusos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_rooms")
data class BuildingRoom(
	var building: Building? = null,
	var attributes: List<Attribute>? = null,
	var capacity: Int? = null,
	var number: String? = null,
	@PrimaryKey
	var id: String = ""
) : Comparable<BuildingRoom> {

	override fun compareTo(other: BuildingRoom) = compareValuesBy(this, other, { number })

	data class Attribute(var id: String, var description: Name, var count: Int?)
}
