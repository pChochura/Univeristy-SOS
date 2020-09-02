package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersSurvey {

	@TypeConverter
	fun toQuestions(json: String): List<Survey.Question>? = Gson().fromJson(json)

	@TypeConverter
	fun fromQuestions(obj: List<Survey.Question>?): String = Gson().toJson(obj)

	@TypeConverter
	fun toAnswers(json: String): List<Survey.Question.Answer>? = Gson().fromJson(json)

	@TypeConverter
	fun fromAnswers(obj: List<Survey.Question.Answer>?): String = Gson().toJson(obj)
}
