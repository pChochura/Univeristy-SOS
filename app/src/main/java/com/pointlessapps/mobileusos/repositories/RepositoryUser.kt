package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.services.ServiceUSOSUser
import com.pointlessapps.mobileusos.utils.CombinedLiveData
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

	@Suppress("UNCHECKED_CAST")
	fun getById(id: String?) = CombinedLiveData(
		serviceUser.getById(id),
		userDao.getById(id)
	) { onlineData, dbData ->
		onlineData ?: dbData
	} as LiveData<User?>
}
