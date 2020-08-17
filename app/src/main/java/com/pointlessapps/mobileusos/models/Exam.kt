package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "table_exams")
@Keep
data class Exam(
	@SerializedName("description")
	var description: Name? = null,
	@SerializedName("name")
	var name: Name? = null,
	@SerializedName("course")
	var course: Course? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
)
