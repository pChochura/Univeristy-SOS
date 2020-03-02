package com.pointlessapps.mobileusos.models

data class Course(
	var courseId: String? = null,
	var courseName: Name? = null
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Course

		if (courseId != other.courseId) return false

		return true
	}

	override fun hashCode() = courseId?.hashCode() ?: 0
}