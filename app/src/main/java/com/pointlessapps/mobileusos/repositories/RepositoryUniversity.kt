package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryUniversity(application: Application) {

	private val universityDao = AppDatabase.init(application).universityDao()
	private val serviceUniversity = ServiceFirebaseUniversity.init()

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

	fun getAll(): LiveData<Pair<List<University>?, Boolean>> {
		val callback = MutableLiveData<Pair<List<University>?, Boolean>>()
		serviceUniversity.getAll().observe {
			callback.postValue(it?.sorted() to true)
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(universityDao.getAll()?.sorted() to false)
		}
		return callback
	}
}
