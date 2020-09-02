package com.pointlessapps.mobileusos.repositories

import com.pointlessapps.mobileusos.services.ServiceUSOSEvent
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType

class RepositoryEvent {

	private val serviceEvent = ServiceUSOSEvent.init()

	fun registerFCMToken(token: String) = ObserverWrapper<Unit> {
		postValue(SourceType.ONLINE) { serviceEvent.registerFCMToken(token) }
	}
}
