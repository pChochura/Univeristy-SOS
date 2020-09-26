package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.forEachDays
import com.pointlessapps.mobileusos.utils.getDayKey
import com.pointlessapps.mobileusos.utils.getMonthKey
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*
import kotlin.collections.set

class ViewModelTimetable(application: Application) : AndroidViewModel(application) {

	private val repositoryTimetable = RepositoryTimetable(application)

	private val weekViewEvents = mutableMapOf<String, MutableSet<WeekView.WeekViewEvent>>()
	private val cache = mutableSetOf<String>()
	val courseEvents = mutableListOf<CourseEvent>()

	fun clearCache() = cache.clear()

	fun prepareForDate(date: Calendar, callback: (() -> Unit)? = null) {
		val days = 7 // Always download for the next 7 days
		var downloading = false
		(date.clone() as Calendar).forEachDays(days) { day ->
			if (!cache.contains(day.getDayKey())) {
				downloading = true
				(day.clone() as Calendar).forEachDays(days) { day2 ->
					cache.add(day2.getDayKey())
				}

				repositoryTimetable.getForDays(date, days).onOnceCallback { value ->
					value.first.filterNotNull().also { list ->
						courseEvents.addAll(list)
						list.map(CourseEvent::toWeekViewEvent)
							.groupBy(WeekView.WeekViewEvent::getMonthKey).forEach {
								if (weekViewEvents.containsKey(it.key)) {
									weekViewEvents[it.key]?.addAll(it.value)
								} else {
									weekViewEvents[it.key] = it.value.toMutableSet()
								}
							}
					}
				}.onFinished { callback?.invoke() }

				return@forEachDays
			}
		}

		if (!downloading) {
			callback?.invoke()
		}
	}

	fun getEventsByMonthYear(month: Int, year: Int) =
		weekViewEvents[Utils.monthKey(month, year)]?.toList() ?: listOf()

	fun getBytUnitIdAndGroupNumber(unitId: String, groupNumber: Int) =
		repositoryTimetable.getByUnitIdAndGroupNumber(unitId, groupNumber)

	fun getByRoomId(roomId: String) = repositoryTimetable.getByRoomId(roomId)

	fun getIncoming() = repositoryTimetable.getForDays(Calendar.getInstance(), 7)
}
