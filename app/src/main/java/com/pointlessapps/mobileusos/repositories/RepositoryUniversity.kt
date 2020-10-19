package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseDatabase
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUniversity(context: Context) {

	private val universityDao = AppDatabase.init(context).universityDao()
	private val serviceUniversity = ServiceFirebaseDatabase.init()

	private fun insert(vararg universities: University) {
		GlobalScope.launch {
			universityDao.insert(*universities)
		}
	}

	fun getAll() = ObserverWrapper<List<University>> {
		postValue { universityDao.getAll().sorted() }
		postValue(SourceType.ONLINE) {
			serviceUniversity.getAllUniversities().also {
				insert(*it.toTypedArray())
			}
		}
	}
}
