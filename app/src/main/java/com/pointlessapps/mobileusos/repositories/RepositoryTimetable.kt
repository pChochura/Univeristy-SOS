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

	fun getByUser(
		userId: String?,
		startTime: Calendar,
		numberOfDays: Int
	): LiveData<List<CourseEvent>> {
		val callback = MutableLiveData<List<CourseEvent>>()
		serviceTimetable.getByUser(userId, startTime, numberOfDays).observe {
			callback.postValue(it)
			insert(*it?.also { events ->
				events.forEach { event ->
					event.userId = userId
				}
			}?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(timetableDao.getByUser(userId, startTime, numberOfDays))
		}
		return callback
	}
}
