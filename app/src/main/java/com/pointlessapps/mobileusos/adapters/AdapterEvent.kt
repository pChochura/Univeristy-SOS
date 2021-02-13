package com.pointlessapps.mobileusos.adapters

import android.content.res.ColorStateList
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemCalendarEventBinding
import com.pointlessapps.mobileusos.models.CalendarEvent

class AdapterEvent : AdapterCore<CalendarEvent, ListItemCalendarEventBinding>(
	mutableListOf(),
	ListItemCalendarEventBinding::class.java
) {

	init {
		setHasStableIds(true)
	}

	override fun onBind(binding: ListItemCalendarEventBinding, position: Int) {
		binding.eventName.text = list[position].name?.toString()
		binding.eventDate.text = binding.root.context.getString(
			R.string.date_period,
			list[position].startDate,
			list[position].endDate
		)
		binding.eventDayOff.isVisible = list[position].isDayOff ?: false
		binding.eventColor.backgroundTintList =
			ColorStateList.valueOf(list[position].getColor(binding.root.context))
	}
}
