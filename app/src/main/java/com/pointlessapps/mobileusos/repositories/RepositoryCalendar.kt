package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.services.ServiceUSOSCalendar
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class RepositoryCalendar(application: Application) {

	private val calendarDao = AppDatabase.init(application).calendarDao()
	private val serviceCalendar = ServiceUSOSCalendar.init()

	private fun insert(vararg calendar: CalendarEvent) {
		GlobalScope.launch {
			calendarDao.insert(*calendar)
		}
	}

	fun getByFaculties(faculties: List<String>, startDate: Date, endDate: Date) =
		ObserverWrapper<List<CalendarEvent>> {
			postValue { calendarDao.getByFaculties(faculties, startDate.time, endDate.time) }
			postValue(SourceType.ONLINE) {
				serviceCalendar.getByFaculties(faculties, startDate, endDate).also {
					insert(*it.toTypedArray())
				}
			}
		}
}
