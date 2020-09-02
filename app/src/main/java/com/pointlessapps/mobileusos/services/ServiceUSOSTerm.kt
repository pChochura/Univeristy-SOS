package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSTerm private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getByIds(ids: List<String>) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(termsRequest(ids))?.run {
					gson.fromJson<Map<String, Term?>>(body).values.toList().filterNotNull()
						.sorted()
				}!!
			}
		}

	suspend fun getAll() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(termsRequest())?.run {
					gson.fromJson<List<Term?>>(body).filterNotNull().sorted()
				}!!
			}
		}

	companion object : Utils.SingletonHolder<ServiceUSOSTerm, Unit>({ ServiceUSOSTerm() })
}
