package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.services.ServiceUSOSApi
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryApi(context: Context) {

	private val facultyDao = AppDatabase.init(context).facultyDao()
	private val serviceApi = ServiceUSOSApi.init()

	private fun insert(vararg faculty: User.Faculty) {
		GlobalScope.launch { facultyDao.insert(*faculty) }
	}

	fun getPrimaryFaculty() =
		ObserverWrapper<User.Faculty?> {
			postValue { facultyDao.getPrimaryFaculty() }
			postValue(SourceType.ONLINE) {
				serviceApi.getPrimaryFaculty().also { insert(it) }
			}
		}
}
