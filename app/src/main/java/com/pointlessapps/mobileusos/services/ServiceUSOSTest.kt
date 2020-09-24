package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSTest private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getAll() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(testsRequest())?.run {
					gson.fromJson<List<Test>>(body)
				}
			}!!
		}

	suspend fun getNodeById(id: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(testNodeByIdRequest(id))?.run {
					gson.fromJson<Test.Node>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSTest, Unit>({ ServiceUSOSTest() })
}
