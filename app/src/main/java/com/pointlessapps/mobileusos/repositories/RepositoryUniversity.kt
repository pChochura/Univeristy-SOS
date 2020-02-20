package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.daos.UniversityDao
import com.pointlessapps.mobileusos.exceptions.ExceptionDatabaseValueNull
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import org.jetbrains.anko.doAsync

class RepositoryUniversity(application: Application) {

	private var universityDao: UniversityDao =
		AppDatabase.getInstance(application)?.universityDao()
			?: throw ExceptionDatabaseValueNull("Database values cannot be null")

	private val universities: MutableLiveData<List<University>> = MutableLiveData()

	init {
		universities.postValue(universityDao.getAll().value)
		doAsync {
			ServiceFirebaseUniversity.getInstance()?.getAll {
				universities.postValue(it)
				update(*it?.toTypedArray() ?: return@getAll)
			}
		}
	}

	fun insert(vararg universities: University) {
		doAsync {
			universityDao.insert(*universities)
		}
	}

	fun update(vararg universities: University) {
		doAsync {
			universityDao.update(*universities)
		}
	}

	fun delete(vararg universities: University) {
		doAsync {
			universityDao.delete(*universities)
		}
	}

	fun getAll() = universities
	fun getByName(name: String) = universityDao.getByName(name)
	fun getByLocation(location: String) = universityDao.getByLocation(location)
}