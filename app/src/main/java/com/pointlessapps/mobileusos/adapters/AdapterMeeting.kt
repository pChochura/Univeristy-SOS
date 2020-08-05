package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.CourseEvent
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class AdapterMeeting(private val extended: Boolean = false) :
	AdapterSimple<CourseEvent>(mutableListOf()) {

	private val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())
	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textDate: AppCompatTextView
	private lateinit var textTime: Chip
	private lateinit var buttonRoom: MaterialButton
	private lateinit var buttonAddToCalendar: MaterialButton

	lateinit var onAddToCalendarClickListener: (CourseEvent) -> Unit
	lateinit var onRoomClickListener: (CourseEvent) -> Unit

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_meeting

	override fun onCreate(root: View, position: Int) {
		super.onCreate(root, position)
		textName = root.find(R.id.meetingName)
		textDate = root.find(R.id.meetingDate)
		textTime = root.find(R.id.meetingTime)
		buttonRoom = root.find(R.id.buttonRoom)
		buttonAddToCalendar = root.find(R.id.buttonAddToCalendar)

		if (extended) {
			textName.visibility = View.VISIBLE
			buttonRoom.visibility = View.GONE
		}
	}

	override fun onBind(root: View, position: Int) {
		textName.text = root.context.getString(
			R.string.meeting_name,
			list[position].courseName.toString(),
			list[position].classtypeName.toString().toLowerCase(Locale.getDefault()),
			list[position].groupNumber
		)
		textDate.text = dateFormat.format(list[position].startTime)
		textTime.text = root.context.getString(
			R.string.time_period,
			list[position].startTime,
			list[position].endTime
		)
		buttonRoom.text = list[position].roomNumber

		buttonRoom.setOnClickListener {
			onRoomClickListener(list[position])
		}

		buttonAddToCalendar.setOnClickListener {
			onAddToCalendarClickListener(list[position])
		}
	}

	override fun update(list: List<CourseEvent>) {
		val sortedList = list.sorted()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}
}
