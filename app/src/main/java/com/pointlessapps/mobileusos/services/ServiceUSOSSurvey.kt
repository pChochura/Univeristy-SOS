package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSSurvey private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getToFill() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(surveysToFillRequest())?.run {
					gson.fromJson<List<Survey>>(body)
				}
			}!!
		}

	suspend fun getById(id: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(surveyRequest(id))?.run {
					gson.fromJson<Survey?>(body)
				}
			}
		}

	suspend fun fillOut(id: String, answers: Map<String, String>, comment: String?) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(fillOutSurveyRequest(id, answers, comment))?.run {
					gson.fromJson<Any>(body)
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSSurvey, Unit>({ ServiceUSOSSurvey() })
}
