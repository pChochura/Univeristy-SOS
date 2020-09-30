package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.CourseEvent
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class AdapterMeeting(private val showCourseName: Boolean = false) :
	AdapterSimple<CourseEvent>(mutableListOf()) {

	private val dateFormat = SimpleDateFormat("EEE, dd MMMM yyyy", Locale.getDefault())

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_meeting
	override fun isCollapsible() = true

	override fun onBind(root: View, position: Int) {
		root.find<AppCompatTextView>(R.id.meetingName).text =
			if (showCourseName) list[position].courseName.toString() else list[position].classtypeName.toString()
		root.find<AppCompatTextView>(R.id.meetingDate).text =
			dateFormat.format(list[position].startTime)
		root.find<Chip>(R.id.meetingTime).text = root.context.getString(
			R.string.time_period,
			list[position].startTime,
			list[position].endTime
		)

		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}
	}

	override fun update(list: List<CourseEvent>) = super.update(list.sorted())
}
