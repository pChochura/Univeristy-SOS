package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSEvent private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun subscribeEvent(eventType: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(subscribeEventRequest(eventType))?.run {
					gson.fromJson<Any>(body)
				}
			}
		}

	suspend fun getAllSubscriptions() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(subscriptionsRequest())?.run {
					gson.fromJson<List<Map<String, String>>>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEvent, Unit>({ ServiceUSOSEvent() })
}
