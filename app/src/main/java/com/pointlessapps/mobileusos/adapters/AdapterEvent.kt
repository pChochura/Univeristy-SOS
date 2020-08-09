package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.CalendarEvent
import org.jetbrains.anko.find

class AdapterEvent : AdapterSimple<CalendarEvent>(mutableListOf()) {

	private lateinit var textName: AppCompatTextView
	private lateinit var textDate: AppCompatTextView
	private lateinit var textDayOff: Chip

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_calendar_event

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.eventName)
		textDate = root.find(R.id.eventDate)
		textDayOff = root.find(R.id.eventDayOff)
	}

	override fun onBind(root: View, position: Int) {
		textName.text = list[position].name?.toString()
		textDate.text = root.context.getString(
			R.string.date_period,
			list[position].startDate,
			list[position].endDate
		)
		textDayOff.isVisible = list[position].isDayOff ?: false
	}
}