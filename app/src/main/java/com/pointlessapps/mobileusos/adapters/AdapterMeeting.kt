package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemMeetingBinding
import com.pointlessapps.mobileusos.models.CourseEvent
import java.text.SimpleDateFormat
import java.util.*

class AdapterMeeting(private val showCourseName: Boolean = false) :
	AdapterCore<CourseEvent, ListItemMeetingBinding>(
		mutableListOf(),
		ListItemMeetingBinding::class.java
	) {

	private val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemMeetingBinding, position: Int) {
		binding.meetingName.text = list[position].name(showCourseName)
		binding.meetingDate.text = dateFormat.format(list[position].startTime)
		binding.meetingTime.text = binding.root.context.getString(
			R.string.time_period,
			list[position].startTime,
			list[position].endTime
		)
	}

	override fun update(list: List<CourseEvent>) = super.update(this.list.toMutableSet().apply {
		addAll(list)
	}.toList().sorted())
}
