package com.pointlessapps.mobileusos.repositories

import com.pointlessapps.mobileusos.services.ServiceUSOSEvent

class RepositoryEvent {

	private val serviceEvent = ServiceUSOSEvent.init()

	fun registerFCMToken(token: String, callback: (Any?) -> Unit) =
		serviceEvent.registerFCMToken(token, callback)
}
