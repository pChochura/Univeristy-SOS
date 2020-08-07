package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSEmail private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getAll(): Callback<List<Email>?> {
		val callback = Callback<List<Email>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(emailsRequest())?.run {
						gson.fromJson<List<Email>>(body)
					}
				}
			)
		}
		return callback
	}

	fun getById(emailId: String): Callback<Email?> {
		val callback = Callback<Email?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(emailRequest(emailId))?.run {
						gson.fromJson<Email>(body)
					}
				}
			)
		}
		return callback
	}

	fun getRecipientsById(emailId: String): Callback<List<Email.Recipient>?> {
		val callback = Callback<List<Email.Recipient>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(emailRecipientsRequest(emailId))?.run {
						gson.fromJson<List<Email.Recipient>>(body)
					}
				}
			)
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEmail, Unit>({ ServiceUSOSEmail() })
}
