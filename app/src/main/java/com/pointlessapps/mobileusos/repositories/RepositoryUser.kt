package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.services.ServiceUSOSUser
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUser(context: Context) {

	private val userDao = AppDatabase.init(context).userDao()
	private val serviceUser = ServiceUSOSUser.init()

	private fun insert(vararg users: User) {
		GlobalScope.launch {
			userDao.insert(*users)
		}
	}

	fun getById(id: String?) = ObserverWrapper<User?> {
		postValue { userDao.getById(id) }
		postValue(SourceType.ONLINE) {
			serviceUser.getById(id).also {
				insert(it?.apply {
					if (id == null) {
						loggedIn = true
					}
				} ?: return@also)
			}
		}
	}

	fun getByIds(ids: List<String>) = ObserverWrapper<List<User>> {
		postValue { userDao.getByIds(ids) }
		postValue(SourceType.ONLINE) {
			serviceUser.getByIds(ids).also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getByQuery(query: String) = ObserverWrapper<List<User>> {
		postValue(SourceType.ONLINE) { serviceUser.getByQuery(query) }
	}
}
