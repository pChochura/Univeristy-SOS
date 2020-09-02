package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSRoom private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getById(roomId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(roomRequest(roomId))?.run {
					gson.fromJson<BuildingRoom>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSRoom, Unit>({ ServiceUSOSRoom() })
}
