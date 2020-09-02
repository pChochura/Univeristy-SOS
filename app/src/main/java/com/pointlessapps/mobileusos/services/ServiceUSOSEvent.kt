package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSEvent private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun registerFCMToken(token: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(registerFCMTokenRequest(token))?.run {
					gson.fromJson<Any>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEvent, Unit>({ ServiceUSOSEvent() })
}
