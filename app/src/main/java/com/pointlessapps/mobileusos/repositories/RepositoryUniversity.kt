package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.daos.UniversityDao
import com.pointlessapps.mobileusos.exceptions.ExceptionDatabaseValueNull
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.anko.doAsync
import kotlin.coroutines.coroutineContext

class RepositoryUniversity(application: Application) {

	private var universityDao: UniversityDao =
		AppDatabase.getInstance(application)?.universityDao()
			?: throw ExceptionDatabaseValueNull("Database values cannot be null")

	private val universities: MutableLiveData<List<University>> = MutableLiveData()

	init {
		runBlocking {
			universities.postValue(universityDao.getAll().value)
			ServiceFirebaseUniversity.getInstance()?.getAll {
				universities.postValue(it)
				insert(*it?.toTypedArray() ?: return@getAll)
			}
		}
	}

	fun insert(vararg universities: University) {
		runBlocking {
			universityDao.insert(*universities)
		}
	}

	fun update(vararg universities: University) {
		runBlocking {
			universityDao.update(*universities)
		}
	}

	fun delete(vararg universities: University) {
		runBlocking {
			universityDao.delete(*universities)
		}
	}

	fun getAll() = universities
}