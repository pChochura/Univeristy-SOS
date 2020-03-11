package com.pointlessapps.mobileusos.services

import android.util.Log
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync
import java.util.*

class ServiceUSOSTimetable private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByUser(userId: String?, startTime: Calendar, numberOfDays: Int): Callback<List<CourseEvent>?> {
		val callback = Callback<List<CourseEvent>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(timetableRequest(userId, startTime, numberOfDays))?.run {
						gson.fromJson<List<CourseEvent>>(body.also {
							Log.d("LOG!", "body: $it")
						})
					}
				}
			)
		}
		return callback
	}

	companion object : Utils.SingletonHolder<ServiceUSOSTimetable, Unit>({ ServiceUSOSTimetable() })
}