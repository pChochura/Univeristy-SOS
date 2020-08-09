package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_buildings")
data class Building(
	var location: Location? = null,
	@ColumnInfo(name = "static_map_urls")
	var staticMapUrls: Map<String, String>? = null,
	@ColumnInfo(name = "campus_name")
	var campusName: Name? = null,
	@ColumnInfo(name = "phone_numbers")
	var phoneNumbers: List<String>? = null,
	@ColumnInfo(name = "all_phone_numbers")
	var allPhoneNumbers: List<PhoneNumber>? = null,
	var rooms: List<BuildingRoom>? = null,
	var name: Name? = null,
	@PrimaryKey
	var id: String = ""
) {

	data class Location(var long: Float, var lat: Float)

	data class PhoneNumber(var number: String, var comment: Name? = null)
}
