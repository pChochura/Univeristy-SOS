package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersTestNodes {

	@TypeConverter
	fun toStudentPoints(json: String): Test.StudentPoint? = Gson().fromJson(json)

	@TypeConverter
	fun toGradeNodeDetails(json: String): Test.Node.GradeDetails? = Gson().fromJson(json)

	@TypeConverter
	fun toSubNodes(json: String): List<Test.Node>? = Gson().fromJson(json)

	@TypeConverter
	fun toStats(json: String): List<Map<String, String>>? = Gson().fromJson(json)
}
