package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
}
