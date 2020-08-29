package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.fromJson

@TypeConverters
class ConvertersGroup {

	@TypeConverter
	fun toListUser(user: String): List<User>? = Gson().fromJson(user)
}
