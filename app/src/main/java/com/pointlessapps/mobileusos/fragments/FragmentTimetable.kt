package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getWeekViewSettings
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.views.WeekView
import kotlinx.android.synthetic.main.fragment_timetable.*
import java.util.*

class FragmentTimetable : FragmentBase() {

	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_timetable
	override fun getNavigationIcon() = R.drawable.ic_timetable
	override fun getNavigationName() = R.string.timetable

	override fun created() {
		prepareWeekView()
		observeTimetableData()

		forceRefresh = true
	}

	private fun observeTimetableData(startTime: Calendar = Calendar.getInstance()) {
		viewModelTimetable.getForDaysByUser(startTime).observe(this@FragmentTimetable) {
			weekView.refreshDataset()
		}
	}

	private fun prepareWeekView() {
		root().post {
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
			weekView.setEventClickListener { event, _ ->
				showEventInfo(event)
			}
		}
	}

	private fun showEventInfo(event: WeekView.WeekViewEvent) {

	}
}