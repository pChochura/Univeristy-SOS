package com.pointlessapps.mobileusos.fragments

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getWeekViewSettings
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.pointlessapps.mobileusos.views.WeekView
import kotlinx.android.synthetic.main.dialog_show_event.*
import kotlinx.android.synthetic.main.fragment_timetable.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentTimetable : FragmentBase() {

	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_timetable
	override fun getNavigationIcon() = R.drawable.ic_timetable
	override fun getNavigationName() = R.string.timetable

	override fun created() {
		root().post {
			prepareClickListeners()
			prepareWeekView()
			observeTimetableData()
		}
	}

	private fun prepareClickListeners() {
		buttonRefresh.setOnClickListener { weekView.refreshDataset() }
	}

	private fun observeTimetableData(startTime: Calendar = Calendar.getInstance()) {
		viewModelTimetable.getForDays(startTime).observe(this@FragmentTimetable) {
			weekView.refreshDataset()
		}
	}

	private fun prepareWeekView() {
		Preferences.get().getWeekViewSettings()?.apply {
			weekView.setStartHour(startHour)
			weekView.setEndHour(endHour)
			weekView.setVisibleDays(numberOfVisibleDays)
			weekView.setEventTextSize(eventTextSize)
		}
		weekView.setScrollListener { newFirstVisibleDay, _ ->
			viewModelTimetable.setStartTime(newFirstVisibleDay)
		}
		weekView.setMonthChangeListener { newYear, newMonth ->
			return@setMonthChangeListener viewModelTimetable.getEventsByMonthYear(newMonth, newYear)
		}
		weekView.setEventClickListener { event, _ -> showEventInfo(event) }
	}

	private fun showEventInfo(weekViewEvent: WeekView.WeekViewEvent) {
		val event = viewModelTimetable.courseEvents.find { it.compositeId() == weekViewEvent.id }

		if (event == null) {
			Snackbar.make(
				view ?: return,
				"Something went wrong while downloading this event",
				Snackbar.LENGTH_SHORT
			)

			return
		}

		DialogUtil.create(requireContext(), R.layout.dialog_show_event, { dialog ->
			val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
			dialog.eventName.text = event.courseName.toString()
			dialog.eventStartTime.text = hourFormat.format(event.startTime.time)
			dialog.eventEndTime.text = hourFormat.format(event.endTime?.time ?: return@create)
			dialog.eventType.text = event.classtypeName.toString()
			dialog.buttonRoom.text = event.roomNumber
			dialog.buttonBuilding.text = event.buildingName.toString()
			viewModelUser.getUserById(event.lecturerIds?.firstOrNull() ?: return@create)
				.observe(this) {
					dialog.buttonLecturer.text = it?.name()
				}

			dialog.buttonLecturer.setOnClickListener {
				onChangeFragmentListener?.invoke(
					FragmentUser(
						event.lecturerIds?.firstOrNull() ?: return@setOnClickListener
					)
				)

				dialog.dismiss()
			}

			dialog.buttonRoom.setOnClickListener {
				onChangeFragmentListener?.invoke(FragmentRoom(event.roomNumber, event.roomId ?: ""))

				dialog.dismiss()
			}

			dialog.buttonBuilding.setOnClickListener {
				onChangeFragmentListener?.invoke(
					FragmentBuilding(
						Building(
							id = event.buildingId ?: return@setOnClickListener
						)
					)
				)

				dialog.dismiss()
			}
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ConstraintLayout.LayoutParams.WRAP_CONTENT)
	}
}
