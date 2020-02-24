package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.daos.UniversityDao
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUniversity(application: Application) {

	private var universityDao: UniversityDao = AppDatabase.init(application).universityDao()

	private val universities: MutableLiveData<List<University>> = MutableLiveData()

	init {
		GlobalScope.launch {
			universities.postValue(universityDao.getAll())
			ServiceFirebaseUniversity.init().getAll {
				universities.postValue(it)
				insert(*it?.toTypedArray() ?: return@getAll)
			}
		}
	}

	fun insert(vararg universities: University) {
		GlobalScope.launch {
			universityDao.insert(*universities)
		}
	}

	fun update(vararg universities: University) {
		GlobalScope.launch {
			universityDao.update(*universities)
		}
	}

	fun delete(vararg universities: University) {
		GlobalScope.launch {
			universityDao.delete(*universities)
		}
	}

	fun getAll() = universities
}