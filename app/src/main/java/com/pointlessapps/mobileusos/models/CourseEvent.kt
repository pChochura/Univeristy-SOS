package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.*

@Entity(tableName = "table_course_events", primaryKeys = ["course_id", "unit_id", "start_time"])
data class CourseEvent(
	@ColumnInfo(name = "course_id")
	var courseId: String = "",
	@ColumnInfo(name = "unit_id")
	var unitId: String = "",
	@ColumnInfo(name = "room_id")
	var roomId: Long = 0,
	@ColumnInfo(name = "course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "start_time")
	var startTime: Date = Date(),
	@ColumnInfo(name = "end_time")
	var endTime: Date? = null,
	@ColumnInfo(name = "building_name")
	var buildingName: Name? = null,
	@ColumnInfo(name = "building_id")
	var buildingId: String? = null,
	@ColumnInfo(name = "room_number")
	var roomNumber: String? = null,
	@ColumnInfo(name = "group_number")
	var groupNumber: String? = null,
	var frequency: String? = null,
	@ColumnInfo(name = "classtype_id")
	var classtypeId: String? = null,
	@ColumnInfo(name = "classtype_name")
	var classtypeName: Name? = null,
	@ColumnInfo(name = "lecturer_ids")
	var lecturerIds: List<String>? = null,
	@ColumnInfo(name = "user_id")
	var userId: String? = null
)
