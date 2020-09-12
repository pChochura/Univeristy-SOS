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

	private fun deleteById(id: String) {
		GlobalScope.launch {
			emailDao.deleteById(id)
		}
	}

	private fun insert(vararg email: Email) {
		GlobalScope.launch {
			emailDao.insertOnly(*email)
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

	fun delete(id: String) =
		ObserverWrapper<Any?> {
			postValue { deleteById(id) }
			postValue(SourceType.ONLINE) {
				serviceEmail.delete(id)
			}
		}

	fun create(subject: String, content: String) =
		ObserverWrapper<String?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.create(
					subject,
					content
				)
			}
		}

	fun update(id: String, subject: String, content: String) =
		ObserverWrapper<Any?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.update(
					id,
					subject,
					content
				)
			}
		}

	fun send(id: String) =
		ObserverWrapper<Any?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.send(id)
			}
		}

	fun updateRecipients(id: String, userIds: List<String>, emails: List<String>) =
		ObserverWrapper<Any?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.updateRecipients(
					id,
					userIds,
					emails
				)
			}
		}

	fun refreshRecipients(id: String) =
		ObserverWrapper<Any?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.refreshRecipients(id)
			}
		}

	fun addAttachment(id: String, data: ByteArray, filename: String) =
		ObserverWrapper<String?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.addAttachment(
					id,
					data,
					filename
				)
			}
		}

	fun deleteAttachment(id: String) =
		ObserverWrapper<Any?> {
			postValue(SourceType.ONLINE) {
				serviceEmail.deleteAttachment(id)
			}
		}
}
