package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync
import java.util.*

class ServiceUSOSCalendar private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByFaculties(
		faculties: List<String>,
		startDate: Date,
		endDate: Date
	): Callback<List<CalendarEvent>?> {
		val callback = Callback<List<CalendarEvent>?>()
		val data = mutableListOf<CalendarEvent>()
		doAsync {
			faculties.forEach { faculty ->
				callback.post(
					data.apply {
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
							} ?: listOf()
						)
					}
				)
			}
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSCalendar, Unit>({ ServiceUSOSCalendar() })
}
