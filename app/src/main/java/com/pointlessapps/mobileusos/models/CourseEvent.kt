package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getEventColorByClassType
import com.pointlessapps.mobileusos.views.WeekView
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
	var lecturerIds: List<String>? = null
) : Comparable<CourseEvent> {

	private fun compositeId() =
		(courseId.hashCode() * 31 + unitId.hashCode()) * 31 + startTime.hashCode().toLong()

	private fun compositeName() =
		"${courseName.toString()}${if (roomNumber.isNullOrBlank()) "" else " ($roomNumber)"} - ${classtypeName.toString()}"

	fun toWeekViewEvent() = WeekView.WeekViewEvent(
		compositeId(),
		compositeName(),
		Calendar.getInstance().apply {
			time = startTime
		},
		Calendar.getInstance().apply {
			time = endTime!!
		}).apply {
		color = Preferences.get().getEventColorByClassType(classtypeId ?: return@apply)
	}

	override fun compareTo(other: CourseEvent) = startTime.compareTo(other.startTime)
}
