package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.utils.*
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

class ViewModelTimetable(application: Application) : AndroidViewModel(application) {

	private val repositoryTimetable = RepositoryTimetable(application)

	private var userId: String? = null
	private var numberOfDays: Int = 7
	private val startTime = MutableLiveData<Calendar>()
	private var timetableForDays = startTime.switchMap {
		repositoryTimetable.getForDaysByUser(userId, it, numberOfDays).map { courseEvents ->
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
		this.startTime.value = startTime
		this.userId = userId
		this.numberOfDays = numberOfDays
		return timetableForDays
	}

	fun setStartTime(startTime: Calendar) {
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
				this.startTime.value = date
				numberOfDays = startNumberOfDays
				return@forEachDaysIndexed
			}
		}
	}

	fun getEventsByMonthYear(month: Int, year: Int) =
		weekViewEvents[Utils.monthKey(month, year)]?.toList() ?: listOf()
}