package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_exam_reports")
@Keep
data class ExamReport(
	@ColumnInfo(name = "grades_distribution")
	@SerializedName("grades_distribution")
	var gradesDistribution: List<Distribution>? = null,
	@ColumnInfo(name = "description")
	@SerializedName("description")
	var description: Name? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) {
	@Keep
	data class Distribution(
		@SerializedName("grade_symbol")
		var gradeSymbol: String,
		@SerializedName("percentage")
		var percentage: String
	)
}
