package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "table_groups", primaryKeys = ["course_id", "term_id", "class_type_id"])
data class Group(
	@ColumnInfo(name = "course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	var termId: String = "",
	@ColumnInfo(name = "class_type_id")
	var classTypeId: String = "",
	@ColumnInfo(name = "class_type")
	var classType: Name? = null,
	@ColumnInfo(name = "course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "group_number")
	var groupNumber: Int = 0,
	var participants: List<User>? = null,
	var lecturers: List<User>? = null
) : Comparable<Group> {

	override fun compareTo(other: Group) =
		compareValuesBy(this, other, { it.courseId }, { it.courseName })
}
