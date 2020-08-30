package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync
import java.util.*

class ServiceUSOSTimetable private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getForDays(
		startTime: Calendar,
		numberOfDays: Int
	): Callback<List<CourseEvent>?> {
		val callback = Callback<List<CourseEvent>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(timetableRequest(startTime, numberOfDays))?.run {
						gson.fromJson<List<CourseEvent>>(body)
					}
				}
			)
		}
		return callback
	}

	fun getByUnitIdAndGroupNumber(unitId: String, groupNumber: Int): Callback<List<CourseEvent>?> {
		val callback = Callback<List<CourseEvent>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(timetableRequest(unitId, groupNumber))?.run {
						gson.fromJson<List<CourseEvent>>(body)
					}
				}
			)
		}
		return callback
	}

	fun getByRoomId(roomId: String): Callback<List<CourseEvent>?> {
		val callback = Callback<List<CourseEvent>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(timetableRequest(roomId))?.run {
						gson.fromJson<List<CourseEvent>>(body)
					}
				}
			)
		}
		return callback
	}

	companion object : Utils.SingletonHolder<ServiceUSOSTimetable, Unit>({ ServiceUSOSTimetable() })
}
