package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ServiceUSOSCalendar private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getByFaculties(faculties: List<String>, startDate: Date, endDate: Date) =
		withContext(Dispatchers.IO) {
			mutableListOf<CalendarEvent>().apply {
				faculties.forEach { faculty ->
					addAll(
						clientService.run {
							execute(calendarRequest(faculty, startDate, endDate))?.run {
								gson.fromJson<List<Map<String, Any>>>(body)
									.map { map ->
										gson.fromJson<CalendarEvent>(gson.toJson(map)).apply {
											facId = gson.fromJson<Map<String, String>>(
												gson.toJson(map["faculty"])
											)["id"]
										}
									}
							}
						}!!
					)
				}
			}
		}.toList()

	companion object :
		Utils.SingletonHolder<ServiceUSOSCalendar, Unit>({ ServiceUSOSCalendar() })
}
