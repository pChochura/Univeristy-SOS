package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUniversity(application: Application) {

	private val universityDao = AppDatabase.init(application).universityDao()
	private val serviceUniversity = ServiceFirebaseUniversity.init()

	private fun insert(vararg universities: University) {
		GlobalScope.launch {
			universityDao.insert(*universities)
		}
	}

	fun getAll() = ObserverWrapper<List<University>> {
		postValue { universityDao.getAll().sorted() }
		postValue(SourceType.ONLINE) {
			serviceUniversity.getAll().also {
				insert(*it.toTypedArray())
			}
		}
	}
}
