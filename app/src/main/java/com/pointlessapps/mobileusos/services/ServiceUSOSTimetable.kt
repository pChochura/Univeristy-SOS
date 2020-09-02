package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class ServiceUSOSTimetable private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getForDays(startTime: Calendar, numberOfDays: Int) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(timetableRequest(startTime, numberOfDays))?.run {
					gson.fromJson<List<CourseEvent>>(body)
				}
			}!!
		}

	suspend fun getByUnitIdAndGroupNumber(unitId: String, groupNumber: Int) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(timetableRequest(unitId, groupNumber))?.run {
					gson.fromJson<List<CourseEvent>>(body)
				}
			}!!
		}

	suspend fun getByRoomId(roomId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(timetableRequest(roomId))?.run {
					gson.fromJson<List<CourseEvent>>(body)
				}
			}!!
		}

	companion object : Utils.SingletonHolder<ServiceUSOSTimetable, Unit>({ ServiceUSOSTimetable() })
}
