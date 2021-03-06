package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_grades", primaryKeys = ["course_id", "term_id"])
@Keep
data class Grade(
	@ColumnInfo(name = "value_symbol")
	@SerializedName("value_symbol")
	var valueSymbol: String? = null,
	@ColumnInfo(name = "value_description")
	@SerializedName("value_description")
	var valueDescription: Name? = null,
	@ColumnInfo(name = "counts_into_average")
	@SerializedName("counts_into_average")
	var countsIntoAverage: String? = null,
	@ColumnInfo(name = "exam_id")
	@SerializedName("exam_id")
	var examId: String? = null,
	@ColumnInfo(name = "exam_session_number")
	@SerializedName("exam_session_number")
	var examSessionNumber: Int = 0,
	@ColumnInfo(name = "comment")
	@SerializedName("comment")
	var comment: String? = null,
	@ColumnInfo(name = "modification_author")
	@SerializedName("modification_author")
	var modificationAuthor: User? = null,
	@ColumnInfo(name = "date_modified")
	@SerializedName("date_modified")
	var dateModified: Date? = null,
	@ColumnInfo(name = "course_name")
	@SerializedName("course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "course_id")
	@SerializedName("course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	@SerializedName("term_id")
	var termId: String = "",
	@ColumnInfo(name = "course_edition")
	@SerializedName("course_edition")
	var courseEdition: Course? = null
) : Comparable<Grade> {

	override fun compareTo(other: Grade) = compareValuesBy(other, this, { it.courseId })

	override fun toString() = "$valueSymbol"

	@Keep
	class GradeType(
		@ColumnInfo(name = "values")
		@SerializedName("values")
		var values: List<Value>? = null,
		@ColumnInfo(name = "name")
		@SerializedName("name")
		var name: Name? = null,
		@PrimaryKey
		@ColumnInfo(name = "id")
		@SerializedName("id")
		var id: String = "",
	) {
		@Keep
		class Value(
			@ColumnInfo(name = "name")
			@SerializedName("name")
			var name: Name? = null,
			@ColumnInfo(name = "decimal_value")
			@SerializedName("decimal_value")
			var decimalValue: Int? = null,
			@ColumnInfo(name = "symbol")
			@SerializedName("symbol")
			var symbol: String? = null,
			@PrimaryKey(autoGenerate = true)
			@ColumnInfo(name = "id")
			@SerializedName("id")
			var id: Int = 0,
		)
	}
}
