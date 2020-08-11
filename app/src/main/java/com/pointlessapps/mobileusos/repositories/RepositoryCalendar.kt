package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.services.ServiceUSOSCalendar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RepositoryCalendar(application: Application) {

	private val calendarDao = AppDatabase.init(application).calendarDao()
	private val serviceCalendar = ServiceUSOSCalendar.init()

	fun insert(vararg calendar: CalendarEvent) {
		GlobalScope.launch {
			calendarDao.insert(*calendar)
		}
	}

	fun update(vararg calendar: CalendarEvent) {
		GlobalScope.launch {
			calendarDao.update(*calendar)
		}
	}

	fun delete(vararg calendar: CalendarEvent) {
		GlobalScope.launch {
			calendarDao.delete(*calendar)
		}
	}

	fun getByFaculties(
		faculties: List<String>,
		startDate: Date,
		endDate: Date
	): LiveData<List<CalendarEvent>?> {
		val callback = MutableLiveData<List<CalendarEvent>?>()
		serviceCalendar.getByFaculties(faculties, startDate, endDate).observe {
			callback.postValue(it ?: return@observe)
			insert(*it.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(calendarDao.getByFaculties(faculties, startDate.time, endDate.time))
		}
		return callback
	}
}
