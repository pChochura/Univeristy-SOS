package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(
	tableName = "table_groups",
	primaryKeys = ["course_id", "term_id", "class_type_id"],
	ignoredColumns = ["grade"]
)
@Keep
data class Course(
	@ColumnInfo(name = "course_unit_id")
	@SerializedName("course_unit_id")
	var courseUnitId: String = "",
	@ColumnInfo(name = "course_id")
	@SerializedName("course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	@SerializedName("term_id")
	var termId: String = "",
	@ColumnInfo(name = "fac_id")
	@SerializedName("fac_id")
	var facId: String? = null,
	@ColumnInfo(name = "class_type_id")
	@SerializedName("class_type_id")
	var classTypeId: String = "",
	@ColumnInfo(name = "class_type")
	@SerializedName("class_type")
	var classType: Name? = null,
	@ColumnInfo(name = "course_name")
	@SerializedName("course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "course_learning_outcomes")
	@SerializedName("course_learning_outcomes")
	var courseLearningOutcomes: Name? = null,
	@ColumnInfo(name = "course_description")
	@SerializedName("course_description")
	var courseDescription: Name? = null,
	@ColumnInfo(name = "course_assessment_criteria")
	@SerializedName("course_assessment_criteria")
	var courseAssessmentCriteria: Name? = null,
	@ColumnInfo(name = "group_number")
	@SerializedName("group_number")
	var groupNumber: Int = 0,
	@SerializedName("participants")
	var participants: List<User>? = null,
	@SerializedName("lecturers")
	var lecturers: List<User>? = null,
	@SerializedName("grade")
	var grade: Grade? = null
) : Comparable<Course> {

	override fun compareTo(other: Course) =
		compareValuesBy(this, other, { it.courseId }, { it.courseName })
}
