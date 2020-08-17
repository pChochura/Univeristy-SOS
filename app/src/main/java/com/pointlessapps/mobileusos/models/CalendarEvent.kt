package com.pointlessapps.mobileusos.models

import android.content.Context
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.R
import java.util.*

@Entity(tableName = "table_calendar_events")
@Keep
data class CalendarEvent(
	@ColumnInfo(name = "is_day_off")
	@SerializedName("is_day_off")
	var isDayOff: Boolean? = null,
	@SerializedName("type")
	var type: String? = null,
	@SerializedName("facId")
	var facId: String? = null,
	@ColumnInfo(name = "end_date")
	@SerializedName("end_date")
	var endDate: Date? = null,
	@ColumnInfo(name = "start_date")
	@SerializedName("start_date")
	var startDate: Date? = null,
	@SerializedName("name")
	var name: Name? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) : Comparable<CalendarEvent> {
	override fun compareTo(other: CalendarEvent) = compareValuesBy(this, other, { it.startDate })

	fun getColor(context: Context) = ContextCompat.getColor(
		context, when (type) {
			"break" -> R.color.color1
			"public_holidays" -> R.color.color2
			"exam_session" -> R.color.color3
			"academic_year" -> R.color.color4
			else -> R.color.colorAccent
		}
	)
}
