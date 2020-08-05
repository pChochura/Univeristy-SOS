package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.ExamReport
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSExamReport private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(examId: String): Callback<ExamReport?> {
		val callback = Callback<ExamReport?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(examReportRequest(examId))?.run {
						gson.fromJson<ExamReport>(body)
					}
				}
			)
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSExamReport, Unit>({ ServiceUSOSExamReport() })
}
