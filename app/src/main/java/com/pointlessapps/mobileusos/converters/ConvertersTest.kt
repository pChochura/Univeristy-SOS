package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersTest {

	@TypeConverter
	fun toTestCourseEdition(json: String): Test.CourseEdition? = Gson().fromJson(json)

	@TypeConverter
	fun toTestGroups(json: String): List<Test.Group>? = Gson().fromJson(json)

	@TypeConverter
	fun toTestCourse(json: String): Test.Course? = Gson().fromJson(json)

	@TypeConverter
	fun toTestNode(json: String): Test.Node? = Gson().fromJson(json)

	@TypeConverter
	fun toTestNodeDetails(json: String): Test.Node.FolderDetails? = Gson().fromJson(json)
}
