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

	fun create(subject: String, content: String, callback: (String?) -> Unit) {
		doAsync {
			callback(
				clientService.run {
					execute(createEmailRequest(subject, content))?.run {
						gson.fromJson<Map<String, String>>(body)["message_id"]
					}
				}
			)
		}
	}

	fun updateRecipients(
		id: String,
		userIds: List<String>,
		emails: List<String>,
		callback: (Any?) -> Unit
	) {
		doAsync {
			callback(
				clientService.run {
					execute(updateEmailRecipientsRequest(id, userIds, emails))?.run {
						gson.fromJson<Any?>(body)
					}
				}
			)
		}
	}

	fun addAttachment(id: String, data: ByteArray, filename: String, callback: (String?) -> Unit) {
		doAsync {
			callback(
				clientService.run {
					execute(addEmailAttachmentRequest(id, data, filename))?.run {
						gson.fromJson<Map<String, String>>(body)["attachment_id"]
					}
				}
			)
		}
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEmail, Unit>({ ServiceUSOSEmail() })
}
