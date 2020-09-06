package com.pointlessapps.mobileusos.adapters

import android.content.res.ColorStateList
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.CalendarEvent
import org.jetbrains.anko.find

class AdapterEvent : AdapterSimple<CalendarEvent>(mutableListOf()) {

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_calendar_event

	override fun onBind(root: View, position: Int) {
		root.find<AppCompatTextView>(R.id.eventName).text = list[position].name?.toString()
		root.find<AppCompatTextView>(R.id.eventDate).text = root.context.getString(
			R.string.date_period,
			list[position].startDate,
			list[position].endDate
		)
		root.find<Chip>(R.id.eventDayOff).isVisible = list[position].isDayOff ?: false
		root.find<View>(R.id.eventColor).backgroundTintList =
			ColorStateList.valueOf(list[position].getColor(root.context))
	}
}
