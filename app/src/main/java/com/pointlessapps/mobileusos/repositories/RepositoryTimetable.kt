package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getTimetableMissingBreaks
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.services.ServiceUSOSTimetable
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class RepositoryTimetable(context: Context) {

	private val timetableDao = AppDatabase.init(context).timetableDao()
	private val serviceTimetable = ServiceUSOSTimetable.init()

	fun insert(vararg courseEvents: CourseEvent) {
		GlobalScope.launch {
			timetableDao.insert(*courseEvents)
		}
	}

	fun deleteByCompositeId(courseId: String, unitId: String, startTime: Long) {
		GlobalScope.launch {
			timetableDao.deleteByCompositeId(courseId, unitId, startTime)
		}
	}

	fun getForDays(startTime: Calendar, numberOfDays: Int) = ObserverWrapper<List<CourseEvent?>> {
		postValue {
			timetableDao.getForDays(
				startTime.timeInMillis,
				startTime.timeInMillis + TimeUnit.DAYS.toMillis(numberOfDays.toLong())
			)
		}
		postValue(SourceType.ONLINE) {
			setBreaks(serviceTimetable.getForDays(startTime, numberOfDays)).also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getByUnitIdAndGroupNumber(unitId: String, groupNumber: Int) =
		ObserverWrapper<List<CourseEvent>> {
			postValue(SourceType.ONLINE) {
				serviceTimetable.getByUnitIdAndGroupNumber(unitId, groupNumber)
			}
		}

	fun getByRoomId(roomId: String) = ObserverWrapper<List<CourseEvent>> {
		postValue(SourceType.ONLINE) { setBreaks(serviceTimetable.getByRoomId(roomId)) }
	}

	private fun setBreaks(courses: List<CourseEvent>): List<CourseEvent> {
		if (!Preferences.get().getTimetableMissingBreaks()) {
			return courses
		}

		val breakLength = -15
		val calendar = Calendar.getInstance()
		return courses.sorted().also {
			for (i in 0 until it.lastIndex) {
				if (it[i].endTime!!.compareTo(it[i + 1].startTime) == 0) {
					it.forEach { course ->
						course.endTime!!.time = calendar.apply {
							time = course.endTime!!
							add(Calendar.MINUTE, breakLength)
						}.timeInMillis
					}
					break
				}
			}
		}
	}
}
