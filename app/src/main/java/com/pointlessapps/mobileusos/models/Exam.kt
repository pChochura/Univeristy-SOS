package com.pointlessapps.mobileusos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_exams")
data class Exam(
	var description: Name? = null,
	var name: Name? = null,
	var course: Course? = null,
	@PrimaryKey
	var id: String = ""
)
