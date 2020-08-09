package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
	tableName = "table_groups",
	primaryKeys = ["course_id", "term_id", "class_type_id"],
	ignoredColumns = ["grade"]
)
data class Course(
	@ColumnInfo(name = "course_unit_id")
	var courseUnitId: String = "",
	@ColumnInfo(name = "course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	var termId: String = "",
	@ColumnInfo(name = "fac_id")
	var facId: String? = null,
	@ColumnInfo(name = "class_type_id")
	var classTypeId: String = "",
	@ColumnInfo(name = "class_type")
	var classType: Name? = null,
	@ColumnInfo(name = "course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "course_learning_outcomes")
	var courseLearningOutcomes: Name? = null,
	@ColumnInfo(name = "course_description")
	var courseDescription: Name? = null,
	@ColumnInfo(name = "course_assessment_criteria")
	var courseAssessmentCriteria: Name? = null,
	@ColumnInfo(name = "group_number")
	var groupNumber: Int = 0,
	var participants: List<User>? = null,
	var lecturers: List<User>? = null,
	var grade: Grade? = null
) : Comparable<Course> {

	override fun compareTo(other: Course) =
		compareValuesBy(this, other, { it.courseId }, { it.courseName })
}
