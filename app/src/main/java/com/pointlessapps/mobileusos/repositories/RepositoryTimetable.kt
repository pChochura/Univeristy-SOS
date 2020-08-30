package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.services.ServiceUSOSTimetable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class RepositoryTimetable(application: Application) {

	private val timetableDao = AppDatabase.init(application).timetableDao()
	private val serviceTimetable = ServiceUSOSTimetable.init()

	fun insert(vararg courseEvents: CourseEvent) {
		GlobalScope.launch {
			timetableDao.insert(*courseEvents)
		}
	}

	fun update(vararg courseEvents: CourseEvent) {
		GlobalScope.launch {
			timetableDao.update(*courseEvents)
		}
	}

	fun delete(vararg courseEvents: CourseEvent) {
		GlobalScope.launch {
			timetableDao.delete(*courseEvents)
		}
	}

	fun getForDays(
		startTime: Calendar,
		numberOfDays: Int
	): LiveData<Pair<List<CourseEvent>, Boolean>> {
		startTime.apply {
			set(Calendar.SECOND, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.HOUR_OF_DAY, 1)
		}
		val callback = MutableLiveData<Pair<List<CourseEvent>, Boolean>>()
		serviceTimetable.getForDays(startTime, numberOfDays).observe {
			val finalEvents = setBreaks(it)
			callback.postValue((finalEvents ?: return@observe) to true)
			insert(*finalEvents.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(
				timetableDao.getForDays(
					startTime.timeInMillis,
					startTime.timeInMillis + TimeUnit.DAYS.toMillis(numberOfDays.toLong())
				) to false
			)
		}
		return callback
	}

	fun getForDays(
		startTime: Calendar,
		numberOfDays: Int,
		callback: (Pair<List<CourseEvent>, Boolean>) -> Unit
	) {
		startTime.apply {
			set(Calendar.SECOND, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.HOUR_OF_DAY, 1)
		}
		serviceTimetable.getForDays(startTime, numberOfDays).observe {
			val finalEvents = setBreaks(it)
			callback.invoke((finalEvents ?: return@observe) to true)
			insert(*finalEvents.toTypedArray())
		}
		GlobalScope.launch {
			callback.invoke(
				timetableDao.getForDays(
					startTime.timeInMillis,
					startTime.timeInMillis + TimeUnit.DAYS.toMillis(numberOfDays.toLong())
				) to false
			)
		}
	}

	fun getByUnitIdAndGroupNumber(unitId: String, groupNumber: Int): LiveData<List<CourseEvent>> {
		val callback = MutableLiveData<List<CourseEvent>>()
		serviceTimetable.getByUnitIdAndGroupNumber(unitId, groupNumber).observe {
			val finalEvents = setBreaks(it)
			callback.postValue(finalEvents?.sorted())
		}
		return callback
	}

	fun getByRoomId(roomId: String): LiveData<List<CourseEvent>> {
		val callback = MutableLiveData<List<CourseEvent>>()
		serviceTimetable.getByRoomId(roomId).observe {
			val finalEvents = setBreaks(it)
			callback.postValue(finalEvents?.sorted())
		}
		return callback
	}

	private fun setBreaks(courses: List<CourseEvent>?): List<CourseEvent>? {
		val breakLength = -15
		return courses?.sorted()?.also {
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
