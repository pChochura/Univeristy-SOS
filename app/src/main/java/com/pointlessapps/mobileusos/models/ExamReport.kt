package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_exam_reports")
data class ExamReport(
	@ColumnInfo(name = "grades_distribution")
	var gradesDistribution: List<Distribution>? = null,
	@ColumnInfo(name = "description")
	var description: Name? = null,
	@PrimaryKey
	var id: String = ""
) {
	data class Distribution(var gradeSymbol: String, var percentage: String)
}
