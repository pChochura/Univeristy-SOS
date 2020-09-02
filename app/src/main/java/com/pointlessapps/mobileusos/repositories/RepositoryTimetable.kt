package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.services.ServiceUSOSTimetable
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class RepositoryTimetable(application: Application) {

	private val timetableDao = AppDatabase.init(application).timetableDao()
	private val serviceTimetable = ServiceUSOSTimetable.init()

	private fun insert(vararg courseEvents: CourseEvent) {
		GlobalScope.launch {
			timetableDao.insert(*courseEvents)
		}
	}

	fun getForDays(startTime: Calendar, numberOfDays: Int) = ObserverWrapper<List<CourseEvent>> {
		startTime.apply {
			set(Calendar.SECOND, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.HOUR_OF_DAY, 1)
		}
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
		val breakLength = -15
		return courses.sorted().also {
			for (i in 0 until it.lastIndex) {
				if (it[i].endTime!!.compareTo(it[i + 1].startTime) == 0) {
					it.forEach { course ->
						course.endTime!!.time = GregorianCalendar().apply {
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
