package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.pointlessapps.mobileusos.views.WeekView
import kotlinx.android.synthetic.main.fragment_timetable.view.*
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
			viewModelTimetable.prepareForDate(newFirstVisibleDay.clone() as Calendar) {
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
		val event = viewModelTimetable.courseEvents.find { it?.compositeId() == weekViewEvent.id }

		if (event == null) {
			Snackbar.make(
				view ?: return,
				getString(R.string.event_download_error),
				Snackbar.LENGTH_SHORT
			)

			return
		}

		Utils.showCourseInfo(requireContext(), event, viewModelUser, onChangeFragment)
	}
}
