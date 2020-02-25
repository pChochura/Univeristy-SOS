package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.ServiceFirebaseUniversity
import com.pointlessapps.mobileusos.utils.Callback
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

	fun getAll(): LiveData<List<University>?> {
		val callback = MutableLiveData<List<University>?>()
		serviceUniversity.getAll().observe {
			callback.postValue(it?.sorted())
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(universityDao.getAll().sorted())
		}
		return callback
	}
}