package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSGuide private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getWhole() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(guideRequest())?.run {
					gson.fromJson<List<Chapter>>(body)
				}
			}!!
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSGuide, Unit>({ ServiceUSOSGuide() })
}
