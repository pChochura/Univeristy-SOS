package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.ExamReport
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSExamReport private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getById(examId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(examReportRequest(examId))?.run {
					gson.fromJson<ExamReport>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSExamReport, Unit>({ ServiceUSOSExamReport() })
}
