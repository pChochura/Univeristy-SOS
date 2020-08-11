package com.pointlessapps.mobileusos.models

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pointlessapps.mobileusos.R
import java.util.*

@Entity(tableName = "table_calendar_events")
data class CalendarEvent(
	@ColumnInfo(name = "is_day_off")
	var isDayOff: Boolean? = null,
	var type: String? = null,
	var facId: String? = null,
	@ColumnInfo(name = "end_date")
	var endDate: Date? = null,
	@ColumnInfo(name = "start_date")
	var startDate: Date? = null,
	var name: Name? = null,
	@PrimaryKey
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
