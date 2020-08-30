package com.pointlessapps.mobileusos.fragments

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.pointlessapps.mobileusos.views.WeekView
import kotlinx.android.synthetic.main.dialog_show_event.*
import kotlinx.android.synthetic.main.fragment_timetable.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentTimetable : FragmentBase() {

	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_timetable
	override fun getNavigationIcon() = R.drawable.ic_timetable
	override fun getNavigationName() = R.string.timetable

	override fun created() {
		prepareClickListeners()
		refreshed()
	}

	override fun refreshed() {
		root().horizontalProgressBar.isRefreshing = true
		prepareWeekView()
	}

	private fun prepareClickListeners() {
		root().buttonRefresh.setOnClickListener {
			root().horizontalProgressBar.isRefreshing = true
			viewModelTimetable.clearCache()
			viewModelTimetable.prepareForDate(root().weekView.firstVisibleDay) {
				root().weekView.refreshDataset()
				root().horizontalProgressBar.isRefreshing = false
			}
		}
	}

	private fun prepareWeekView() {
		Preferences.get().apply {
			root().weekView.setStartHour(getTimetableStartHour())
			root().weekView.setEndHour(getTimetableEndHour())
			root().weekView.setVisibleDays(getTimetableVisibleDays())
			root().weekView.setSnappingEnabled(getTimetableSnapToFullDay())
		}
		root().weekView.setScrollListener { newFirstVisibleDay, _ ->
			root().horizontalProgressBar.isRefreshing = true
			viewModelTimetable.prepareForDate(newFirstVisibleDay) {
				root().weekView.refreshDataset()
				root().horizontalProgressBar.isRefreshing = false
			}
		}
		root().weekView.setMonthChangeListener { newYear, newMonth ->
			return@setMonthChangeListener viewModelTimetable.getEventsByMonthYear(newMonth, newYear)
		}
		root().weekView.setEventClickListener { event, _ -> showEventInfo(event) }
	}

	private fun showEventInfo(weekViewEvent: WeekView.WeekViewEvent) {
		val event = viewModelTimetable.courseEvents.find { it.compositeId() == weekViewEvent.id }

		if (event == null) {
			Snackbar.make(
				view ?: return,
				getString(R.string.event_download_error),
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
				.observe(this) { (user, _) ->
					dialog.buttonLecturer.text = user?.name()
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
