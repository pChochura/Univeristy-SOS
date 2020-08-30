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
	): LiveData<Pair<List<CalendarEvent>, Boolean>> {
		val callback = MutableLiveData<Pair<List<CalendarEvent>, Boolean>>()
		serviceCalendar.getByFaculties(faculties, startDate, endDate).observe {
			callback.postValue(it to true)
			insert(*it.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(
				calendarDao.getByFaculties(
					faculties,
					startDate.time,
					endDate.time
				) to false
			)
		}
		return callback
	}
}
