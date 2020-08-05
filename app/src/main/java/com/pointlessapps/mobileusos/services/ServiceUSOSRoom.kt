package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSRoom private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(roomId: String): Callback<BuildingRoom?> {
		val callback = Callback<BuildingRoom?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(roomRequest(roomId))?.run {
						gson.fromJson<BuildingRoom>(body)
					}
				}
			)
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSRoom, Unit>({ ServiceUSOSRoom() })
}
