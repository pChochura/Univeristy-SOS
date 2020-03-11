package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.pointlessapps.mobileusos.models.TimetableUnit
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.utils.*
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

class ViewModelTimetable(application: Application) : AndroidViewModel(application) {

	private val repositoryTimetable = RepositoryTimetable(application)

	private val timetableUnit = MutableLiveData<TimetableUnit>()
	private var timetableForDays = timetableUnit.switchMap {
		repositoryTimetable.getForDaysByUser(it.userId, it.startTime, it.numberOfDays).map { courseEvents ->
			courseEvents.map { courseEvent ->
				daysUpToDate.add(courseEvent.startTime.getDayKey())
				courseEvent.toWeekViewEvent()
			}.groupBy { weekViewEvent ->
				weekViewEvent.getMonthKey()
			}.apply {
				forEach { entry ->
					if (weekViewEvents.containsKey(entry.key)) {
						weekViewEvents[entry.key]?.addAll(entry.value)
					} else {
						weekViewEvents[entry.key] = entry.value.toMutableSet()
					}
				}
			}
		}
	}

	private val weekViewEvents = mutableMapOf<String, MutableSet<WeekView.WeekViewEvent>>()
	private val daysUpToDate = mutableSetOf<String>()

	fun getForDaysByUser(
		startTime: Calendar,
		userId: String? = null,
		numberOfDays: Int = 7
	): LiveData<Map<String, List<WeekView.WeekViewEvent>>> {
		timetableUnit.value = TimetableUnit(userId, numberOfDays, startTime)
		return timetableForDays
	}

	fun setStartTime(startTime: Calendar) {
		var numberOfDays = timetableUnit.value?.numberOfDays ?: 7
		(startTime.clone() as Calendar).forEachDaysIndexed(numberOfDays) { i, date ->
			if (!daysUpToDate.contains(date.getDayKey())) {
				val startNumberOfDays = numberOfDays
				numberOfDays -= i
				(date.clone() as Calendar).forEachDaysReverseIndexed(numberOfDays) { i2, date2 ->
					if (!daysUpToDate.contains(date2.getDayKey())) {
						numberOfDays -= i2
						return@forEachDaysReverseIndexed
					}
				}
				if (timetableUnit.value != null) {
					timetableUnit.value = timetableUnit.value?.copy(startTime = date, numberOfDays = numberOfDays)
				} else {
					timetableUnit.value = TimetableUnit(null, numberOfDays, date)
				}
				numberOfDays = startNumberOfDays
				return@forEachDaysIndexed
			}
		}
	}

	fun getEventsByMonthYear(month: Int, year: Int) =
		weekViewEvents[Utils.monthKey(month, year)]?.toList() ?: listOf()
}