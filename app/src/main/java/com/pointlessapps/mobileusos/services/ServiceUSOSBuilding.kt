package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSBuilding private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getById(buildingId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(buildingRequest(buildingId))?.run {
					gson.fromJson<Building>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSBuilding, Unit>({ ServiceUSOSBuilding() })
}
