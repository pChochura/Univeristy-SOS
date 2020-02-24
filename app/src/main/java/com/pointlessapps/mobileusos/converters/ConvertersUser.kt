package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersUser {

	@TypeConverter
	fun toString(title: Any?) = Gson().toJson(title)

	@TypeConverter
	fun titleFromString(title: String) = Gson().fromJson(title, User.Title::class.java)

	@TypeConverter
	fun photoUrlsFromString(map: String) = Gson().fromJson<Map<String, String>>(map)
}
