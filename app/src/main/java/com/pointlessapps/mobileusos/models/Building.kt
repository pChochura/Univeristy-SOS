package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_buildings")
@Keep
data class Building(
	@SerializedName("location")
	var location: Location? = null,
	@ColumnInfo(name = "static_map_urls")
	@SerializedName("static_map_urls")
	var staticMapUrls: Map<String, String>? = null,
	@ColumnInfo(name = "campus_name")
	@SerializedName("campus_name")
	var campusName: Name? = null,
	@ColumnInfo(name = "phone_numbers")
	@SerializedName("phone_numbers")
	var phoneNumbers: List<String>? = null,
	@ColumnInfo(name = "all_phone_numbers")
	@SerializedName("all_phone_numbers")
	var allPhoneNumbers: List<PhoneNumber>? = null,
	@SerializedName("rooms")
	var rooms: List<BuildingRoom>? = null,
	@SerializedName("name")
	var name: Name? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) {

	@Keep
	data class Location(
		@SerializedName("long")
		var long: Float,
		@SerializedName("lat")
		var lat: Float
	)

	@Keep
	data class PhoneNumber(
		@SerializedName("number")
		var number: String,
		@SerializedName("comment")
		var comment: Name? = null
	)
}
