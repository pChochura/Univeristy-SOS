package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.models.TimetableUnit
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.utils.*
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

class ViewModelTimetable(application: Application) : AndroidViewModel(application) {

	private val repositoryTimetable = RepositoryTimetable(application)

	private val timetableUnit = MutableLiveData<TimetableUnit>()
	private var timetableForDays = timetableUnit.switchMap {
		repositoryTimetable.getForDays(it.startTime, it.numberOfDays)
			.map { (courseEvents, _) ->
				courseEvents.map { courseEvent ->
					this.courseEvents.add(courseEvent)
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
	val courseEvents = mutableListOf<CourseEvent>()

	fun getForDays(
		startTime: Calendar = Calendar.getInstance(),
		numberOfDays: Int = 7
	): LiveData<Map<String, List<WeekView.WeekViewEvent>>> {
		timetableUnit.value = TimetableUnit(numberOfDays, startTime)
		return timetableForDays
	}

	fun getBytUnitIdAndGroupNumber(unitId: String, groupNumber: Int) =
		repositoryTimetable.getByUnitIdAndGroupNumber(unitId, groupNumber)

	fun getByRoomId(roomId: String) = repositoryTimetable.getByRoomId(roomId)

	fun getIncoming() = repositoryTimetable.getForDays(Calendar.getInstance(), 7)

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
				if (numberOfDays > 0) {
					if (timetableUnit.value != null) {
						timetableUnit.value =
							timetableUnit.value?.copy(startTime = date, numberOfDays = numberOfDays)
					} else {
						timetableUnit.value = TimetableUnit(numberOfDays, date)
					}
				}

				numberOfDays = startNumberOfDays
				return@forEachDaysIndexed
			}
		}
	}

	fun getEventsByMonthYear(month: Int, year: Int) =
		weekViewEvents[Utils.monthKey(month, year)]?.toList() ?: listOf()
}
