package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Exam
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSExam private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getByIds(ids: List<String>) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(examRequest(ids))?.run {
					gson.fromJson<Map<String, Exam>>(body).values.toList()
				}
			}!!
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSExam, Unit>({ ServiceUSOSExam() })
}
