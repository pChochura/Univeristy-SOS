package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.FragmentTimetableBinding
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

class FragmentTimetable :
	FragmentCoreImpl<FragmentTimetableBinding>(FragmentTimetableBinding::class.java) {

	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getNavigationIcon() = R.drawable.ic_timetable
	override fun getNavigationName() = R.string.timetable

	override fun created() {
		prepareClickListeners()
		refreshed()
		refreshDataset(true)
	}

	override fun refreshed() {
		binding().horizontalProgressBar.isRefreshing = true
		prepareWeekView()
	}

	private fun prepareClickListeners() {
		binding().buttonRefresh.setOnClickListener { refreshDataset() }
	}

	private fun refreshDataset(clearAll: Boolean = false) {
		binding().horizontalProgressBar.isRefreshing = true
		if (clearAll) {
			viewModelTimetable.clearAll()
		} else {
			viewModelTimetable.clearCache()
		}
		viewModelTimetable.prepareForDate(binding().weekView.firstVisibleDay) {
			binding().weekView.refreshDataset()
			binding().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareWeekView() {
		val prefs = Preferences.get()
		prefs.apply {
			binding().weekView.setStartHour(getTimetableStartHour())
			binding().weekView.setEndHour(getTimetableEndHour())
			binding().weekView.setVisibleDays(getTimetableVisibleDays())
			binding().weekView.setSnappingEnabled(getTimetableSnapToFullDay())
		}
		binding().weekView.setScrollListener { newFirstVisibleDay, _ ->
			binding().horizontalProgressBar.isRefreshing = true
			viewModelTimetable.prepareForDate(newFirstVisibleDay.clone() as Calendar) {
				binding().weekView.refreshDataset()
				binding().horizontalProgressBar.isRefreshing = false
			}
		}
		binding().weekView.setMonthChangeListener { newYear, newMonth ->
			viewModelTimetable.getEventsByMonthYear(newMonth, newYear)
		}
		binding().weekView.setEventClickListener { event, _ -> showEventInfo(event) }
		if (prefs.getTimetableAddEvent()) {
			binding().weekView.setEmptyViewClickListener {
				Utils.showEventAdd(requireContext(), it) {
					refreshDataset()
				}
			}
		}
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

		Utils.showCourseInfo(
			requireContext(), event, viewModelUser, onChangeFragment,
			{
				weekViewEvent.comment = it
				binding().weekView.refreshDataset()
			}, { refreshDataset(true) }
		)
	}
}
