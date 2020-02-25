package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_groups")
data class Group(
	@PrimaryKey
	var courseId: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "term_id")
	var termId: String? = null,
	@ColumnInfo(name = "class_type")
	var classType: Name? = null,
	@ColumnInfo(name = "course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "group_number")
	var groupNumber: Int = 0,
	var participants: List<User>? = null,
	var lecturers: List<User>? = null
): Comparable<Group> {

	override fun compareTo(other: Group) =
		compareValuesBy(this, other, { it.courseId }, { it.courseName })
}
