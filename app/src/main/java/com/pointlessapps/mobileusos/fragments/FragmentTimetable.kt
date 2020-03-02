package com.pointlessapps.mobileusos.fragments

import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
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

		viewModelTimetable.getByUser(Calendar.getInstance()).observe(this) {
			Log.d("LOG!", Gson().toJson(it))
		}
	}

	private fun prepareWeekView() {
		root().post {
			weekView.setStartHour(6)
			weekView.setEndHour(20)
			weekView.setVisibleDays(5)
			weekView.setEventTextSize(12)
			weekView.setScrollListener { newFirstVisibleDay, _ ->
				//			setEventsListFrom(newFirstVisibleDay)
			}
			weekView.setMonthChangeListener { newYear, newMonth ->
				return@setMonthChangeListener listOf<WeekView.WeekViewEvent>() /*allEvents[eventMonthKey.format(GregorianCalendar().apply {
				set(Calendar.MONTH, newMonth)
				set(Calendar.YEAR, newYear)
			}.time)] ?: listOf()*/
			}
			weekView.setEventClickListener { event, _ ->
				//			showEventInfo(event)
			}
		}
	}
}