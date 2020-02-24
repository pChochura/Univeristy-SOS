package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.fromJson

@TypeConverters
class ConvertersGroup {

	@TypeConverter
	fun toListTerm(name: String): List<Term>? = Gson().fromJson(name)

	@TypeConverter
	fun toListUser(name: String): List<User>? = Gson().fromJson(name)
}
