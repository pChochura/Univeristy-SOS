package com.pointlessapps.mobileusos.repositories

import com.pointlessapps.mobileusos.services.ServiceFirebaseDatabase
import com.pointlessapps.mobileusos.services.ServiceUSOSEvent
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryEvent {

	private val serviceEvent = ServiceUSOSEvent.init()
	private val serviceFirebaseDatabase = ServiceFirebaseDatabase.init()

	private val availableSubscriptions =
		listOf("grades/grade", "crstests/user_grade", "crstests/user_point")

	fun registerFCMToken(userId: String, token: String) = ObserverWrapper<Any?> {
		postValue(SourceType.ONLINE) {
			serviceFirebaseDatabase.registerFcmToken(userId, token)
		}
	}

	fun subscribeNotifications(
		userId: String,
		accessToken: String,
		accessTokenSecret: String,
		universityServiceUrl: String,
		surveysIds: List<String>,
		articlesIds: List<String>
	) = ObserverWrapper<Any?> {
		postValue(SourceType.ONLINE) {
			serviceFirebaseDatabase.subscribeNotifications(
				userId,
				accessToken,
				accessTokenSecret,
				universityServiceUrl,
				surveysIds,
				articlesIds
			)
		}
	}

	private fun subscribeEvent(eventType: String) = ObserverWrapper<Any?> {
		postValue(SourceType.ONLINE) { serviceEvent.subscribeEvent(eventType) }
	}

	fun ensureEventSubscription() {
		GlobalScope.launch {
			runCatching {
				val subs = serviceEvent.getAllSubscriptions()
				val newSubs =
					availableSubscriptions.minus(subs?.map { it.getOrDefault("event_type", "") }
						?: listOf())
				newSubs.forEach { subscribeEvent(it) }
			}
		}
	}
}
