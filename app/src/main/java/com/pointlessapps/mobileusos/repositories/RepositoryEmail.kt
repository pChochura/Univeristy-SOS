package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.services.ServiceUSOSEmail
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryEmail(application: Application) {

	private val emailDao = AppDatabase.init(application).emailDao()
	private val serviceEmail = ServiceUSOSEmail.init()

	fun insert(vararg email: Email) {
		GlobalScope.launch {
			emailDao.insert(*email)
		}
	}

	fun update(vararg email: Email) {
		GlobalScope.launch {
			emailDao.update(*email)
		}
	}

	fun delete(vararg email: Email) {
		GlobalScope.launch {
			emailDao.delete(*email)
		}
	}

	fun getAll(): LiveData<List<Email>> {
		val callback = MutableLiveData<List<Email>>()
		serviceEmail.getAll().observe {
			callback.postValue(it?.sorted() ?: return@observe)
			insert(*it.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(emailDao.getAll().sorted())
		}
		return callback
	}

	fun getById(emailId: String): LiveData<Email?> {
		val callback = MutableLiveData<Email?>()
		serviceEmail.getById(emailId).observe {
			callback.postValue(it ?: return@observe)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue(emailDao.getById(emailId))
		}
		return callback
	}

	fun getRecipientsById(emailId: String): LiveData<List<Email.Recipient>?> {
		val callback = MutableLiveData<List<Email.Recipient>?>()
		serviceEmail.getRecipientsById(emailId).observe {
			callback.postValue(it ?: return@observe)
		}
		return callback
	}
}
