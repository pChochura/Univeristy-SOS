package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSEmail private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getAll() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(emailsRequest())?.run {
					gson.fromJson<List<Email>>(body)
				}!!
			}
		}

	suspend fun getById(emailId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(emailRequest(emailId))?.run {
					gson.fromJson<Email>(body)
				}
			}
		}

	suspend fun getRecipientsById(emailId: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(emailRecipientsRequest(emailId))?.run {
					gson.fromJson<List<Email.Recipient>>(body)
				}
			}!!
		}

	suspend fun create(subject: String, content: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(createEmailRequest(subject, content))?.run {
					gson.fromJson<Map<String, String>>(body)["message_id"]
				}
			}
		}

	suspend fun updateRecipients(
		id: String,
		userIds: List<String>,
		emails: List<String>
	) = withContext(Dispatchers.IO) {
		clientService.run {
			execute(updateEmailRecipientsRequest(id, userIds, emails))?.run {
				gson.fromJson<Any>(body)
			}
		}
	}

	suspend fun addAttachment(id: String, data: ByteArray, filename: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(addEmailAttachmentRequest(id, data, filename))?.run {
					gson.fromJson<Map<String, String>>(body)["attachment_id"]
				}
			}
		}

	companion object :
		Utils.SingletonHolder<ServiceUSOSEmail, Unit>({ ServiceUSOSEmail() })
}
