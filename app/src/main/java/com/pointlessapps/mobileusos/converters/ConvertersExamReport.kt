package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.ExamReport
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersExamReport {

	@TypeConverter
	fun toString(distribution: List<ExamReport.Distribution>?): String = Gson().toJson(distribution)

	@TypeConverter
	fun toDistribution(json: String): List<ExamReport.Distribution>? = Gson().fromJson(json)
}
