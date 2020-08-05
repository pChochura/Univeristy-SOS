package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "table_grades", primaryKeys = ["course_id", "term_id"])
data class Grade(
	@ColumnInfo(name = "value_symbol")
	var valueSymbol: String? = null,
	@ColumnInfo(name = "value_description")
	var valueDescription: Name? = null,
	@ColumnInfo(name = "counts_into_average")
	var countsIntoAverage: String? = null,
	@ColumnInfo(name = "exam_id")
	var examId: String? = null,
	@ColumnInfo(name = "exam_session_number")
	var examSessionNumber: Int = 0,
	@ColumnInfo(name = "comment")
	var comment: String? = null,
	@ColumnInfo(name = "modification_author")
	var modificationAuthor: User? = null,
	@ColumnInfo(name = "date_modified")
	var dateModified: Date? = null,
	@ColumnInfo(name = "course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	var termId: String = ""
) : Comparable<Grade> {

	override fun compareTo(other: Grade) = compareValuesBy(other, this, { it.courseId })

	override fun toString() = "$valueSymbol"
}
