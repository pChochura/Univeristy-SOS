package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_buildings")
data class Building(
	var location: Location? = null,
	@ColumnInfo(name = "static_map_urls")
	var staticMapUrls: Map<String, String>? = null,
	var name: Name? = null,
	@PrimaryKey
	var id: String = ""
) {

	data class Location(var long: Float, var lat: Float)
}
