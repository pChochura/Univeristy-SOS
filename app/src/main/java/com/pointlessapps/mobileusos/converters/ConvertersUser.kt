package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersUser {

	@TypeConverter
	fun toTitle(title: String): User.Title? = Gson().fromJson(title)

	@TypeConverter
	fun toEmploymentFunction(employmentFunction: String): List<User.EmploymentFunction>? =
		Gson().fromJson(employmentFunction)

	@TypeConverter
	fun fromEmploymentFunction(employmentFunction: List<User.EmploymentFunction>?): String =
		Gson().toJson(employmentFunction)

	@TypeConverter
	fun toStringStringMap(map: String): Map<String, String>? = Gson().fromJson(map)

	@TypeConverter
	fun toUser(user: String?): User? {
		return Gson().fromJson(user ?: return User())
	}

	@TypeConverter
	fun toFaculty(faculty: String): User.Faculty = Gson().fromJson(faculty)
}
