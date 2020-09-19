package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersSurvey {

	@TypeConverter
	fun toQuestions(json: String): List<Survey.Question>? = Gson().fromJson(json)

	@TypeConverter
	fun fromQuestions(obj: List<Survey.Question>?): String = Gson().toJson(obj)

	@TypeConverter
	fun toCourse(json: String): Course? = Gson().fromJson(json)

	@TypeConverter
	fun fromCourse(obj: Course?): String = Gson().toJson(obj)
}
