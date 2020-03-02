package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersUser {

	@TypeConverter
	fun toTitle(title: String): User.Title? = Gson().fromJson(title)

	@TypeConverter
	fun toStringStringMap(map: String): Map<String, String>? = Gson().fromJson(map)
}