package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSEvent private constructor() {

	private val clientService = ClientUSOSService.init()

	fun registerFCMToken(token: String, callback: (Any?) -> Unit) {
		doAsync {
			callback(
				clientService.run {
					execute(registerFCMTokenRequest(token))?.run {
						gson.fromJson<Any>(body)
					}
				}
			)
		}
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEvent, Unit>({ ServiceUSOSEvent() })
}
