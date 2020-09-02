package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.services.ServiceUSOSEmail
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryEmail(application: Application) {

	private val emailDao = AppDatabase.init(application).emailDao()
	private val serviceEmail = ServiceUSOSEmail.init()

	private fun insert(vararg email: Email) {
		GlobalScope.launch {
			emailDao.insert(*email)
		}
	}

	fun getAll() = ObserverWrapper<List<Email>> {
		postValue { emailDao.getAll().sorted() }
		postValue(SourceType.ONLINE) {
			serviceEmail.getAll().sorted().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getById(emailId: String) = ObserverWrapper<Email?> {
		postValue { emailDao.getById(emailId) }
		postValue(SourceType.ONLINE) {
			serviceEmail.getById(emailId)?.also {
				insert(it)
			}
		}
	}

	fun getRecipientsById(emailId: String) = ObserverWrapper<List<Email.Recipient>> {
		postValue(SourceType.ONLINE) {
			serviceEmail.getRecipientsById(emailId)
		}
	}

	fun create(subject: String, content: String) =
		ObserverWrapper<String?> { postValue { serviceEmail.create(subject, content) } }

	fun updateRecipients(id: String, userIds: List<String>, emails: List<String>) =
		ObserverWrapper<Unit> { postValue { serviceEmail.updateRecipients(id, userIds, emails) } }

	fun addAttachment(id: String, data: ByteArray, filename: String) =
		ObserverWrapper<String?> { postValue { serviceEmail.addAttachment(id, data, filename) } }
}
