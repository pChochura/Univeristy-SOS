package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSApi private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getPrimaryFaculty() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(primaryFacultyRequest())?.run {
					gson.fromJson<Map<String, User.Faculty>>(body)["institution"]
				}
			}!!
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSApi, Unit>({ ServiceUSOSApi() })
}
