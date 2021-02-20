package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getTimetableOutlineRemote
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

@Entity(tableName = "table_course_events", primaryKeys = ["course_id", "unit_id", "start_time"])
@Keep
data class CourseEvent(
	@ColumnInfo(name = "memo")
	@SerializedName("memo")
	var memo: String? = null,
	@ColumnInfo(name = "course_id")
	@SerializedName("course_id")
	var courseId: String = "",
	@ColumnInfo(name = "unit_id")
	@SerializedName("unit_id")
	var unitId: String = "",
	@ColumnInfo(name = "room_id")
	@SerializedName("room_id")
	var roomId: String? = null,
	@ColumnInfo(name = "course_name")
	@SerializedName("course_name")
	var courseName: Name? = null,
	@ColumnInfo(name = "start_time")
	@SerializedName("start_time")
	var startTime: Date = Date(),
	@ColumnInfo(name = "end_time")
	@SerializedName("end_time")
	var endTime: Date? = null,
	@ColumnInfo(name = "end_time_repeating")
	@SerializedName("end_time_repeating")
	var endTimeRepeating: Date? = null,
	@ColumnInfo(name = "building_name")
	@SerializedName("building_name")
	var buildingName: Name? = null,
	@ColumnInfo(name = "building_id")
	@SerializedName("building_id")
	var buildingId: String? = null,
	@ColumnInfo(name = "room_number")
	@SerializedName("room_number")
	var roomNumber: String? = null,
	@ColumnInfo(name = "group_number")
	@SerializedName("group_number")
	var groupNumber: String? = null,
	@ColumnInfo(name = "frequency")
	@SerializedName("frequency")
	var frequency: Long? = null,
	@ColumnInfo(name = "classtype_id")
	@SerializedName("classtype_id")
	var classtypeId: String? = null,
	@ColumnInfo(name = "classtype_name")
	@SerializedName("classtype_name")
	var classtypeName: Name? = null,
	@ColumnInfo(name = "lecturer_ids")
	@SerializedName("lecturer_ids")
	var lecturerIds: List<String>? = null,
	@ColumnInfo(name = "lecturer_name")
	@SerializedName("lecturer_name")
	var lecturerName: String? = null,
	@ColumnInfo(name = "related_user_ids")
	@SerializedName("related_user_ids")
	var relatedUserIds: List<String>? = null,
	@ColumnInfo(name = "color")
	@SerializedName("color")
	var color: Int? = null,
	@ColumnInfo(name = "name")
	@SerializedName("name")
	var name: Name? = null
) : Comparable<CourseEvent> {

	fun compositeId() =
		(courseId.hashCode() * 31 + unitId.hashCode()) * 31 + startTime.hashCode().toLong()

	private fun compositeName() = buildString {
		append(name())
		if (roomNumber?.isNotBlank() == true) append(" (${roomNumber.toString()})")
		if (classtypeName?.isNotEmpty() == true) append(" - ${classtypeName.toString()}")
	}

	fun name(withCourseName: Boolean = true) =
		courseName?.takeIf { withCourseName }?.toString() ?: classtypeName?.toString()
		?: name.toString()

	fun toWeekViewEvent() = WeekView.WeekViewEvent(
		compositeId(),
		compositeName(),
		Calendar.getInstance().apply {
			time = startTime
		},
		Calendar.getInstance().apply {
			time = endTime!!
		},
		memo
	).apply {
		color = this@CourseEvent.color ?: Utils.getColorByClassType(classtypeId)
		frequency = this@CourseEvent.frequency ?: 0
		repeatingEndDate =
			endTimeRepeating?.let { Calendar.getInstance().apply { timeInMillis = it.time } }
		setHasOutline(
			Preferences.get().getTimetableOutlineRemote() &&
					buildingId?.toLowerCase(Locale.forLanguageTag("pl")) == "zdalny"
		)
	}

	override fun compareTo(other: CourseEvent) = startTime.compareTo(other.startTime)

	override fun equals(other: Any?): Boolean {
		if (other == null || other !is CourseEvent) {
			return false
		}

		if (courseId == other.courseId && unitId == other.unitId && startTime.time == other.startTime.time) {
			return true
		}

		return hashCode() == other.hashCode()
	}

	override fun hashCode(): Int {
		var result = courseId.hashCode()
		result = 31 * result + unitId.hashCode()
		result = 31 * result + startTime.time.hashCode()
		return result
	}
}
