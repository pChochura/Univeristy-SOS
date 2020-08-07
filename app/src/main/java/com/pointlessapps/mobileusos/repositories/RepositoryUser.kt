package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.services.ServiceUSOSUser
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUser(application: Application) {

	private val userDao = AppDatabase.init(application).userDao()
	private val serviceUser = ServiceUSOSUser.init()

	fun insert(vararg users: User) {
		GlobalScope.launch {
			userDao.insert(*users)
		}
	}

	fun update(vararg users: User) {
		GlobalScope.launch {
			userDao.update(*users)
		}
	}

	fun delete(vararg users: User) {
		GlobalScope.launch {
			userDao.delete(*users)
		}
	}

	fun getById(id: String?): LiveData<User?> {
		val callback = MutableLiveData<User?>()
		serviceUser.getById(id).observe {
			callback.postValue(it)
			insert(it?.apply {
				if (id == null) {
					loggedIn = true
				}
			} ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(userDao.getById(id))
		}
		return callback
	}

	fun getByQuery(query: String): LiveData<List<User>?> {
		val callback = MutableLiveData<List<User>?>()
		serviceUser.getByQuery(query).observe {
			callback.postValue(it)
		}
		return callback
	}
}
