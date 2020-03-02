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
	var countsIntoAverage: Boolean = false,
	var comment: String? = null,
	@ColumnInfo(name = "date_modified")
	var dateModified: Date? = null,
	@ColumnInfo(name = "course_id")
	var courseId: String = "",
	@ColumnInfo(name = "term_id")
	var termId: String = ""
) {

	override fun toString() = "$valueSymbol"
}