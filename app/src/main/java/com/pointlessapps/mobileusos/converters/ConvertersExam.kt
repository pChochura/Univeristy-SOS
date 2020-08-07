package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersExam {

	@TypeConverter
	fun toString(course: Course?): String = Gson().toJson(course)

	@TypeConverter
	fun toCourse(json: String): Course? = Gson().fromJson(json)
}
