package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.services.ServiceUSOSGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGroup(application: Application) {

	private val groupDao = AppDatabase.init(application).groupDao()
	private val serviceGroup = ServiceUSOSGroup.init()

	fun insert(vararg courses: Course) {
		GlobalScope.launch {
			groupDao.insert(*courses)
		}
	}

	fun update(vararg courses: Course) {
		GlobalScope.launch {
			groupDao.update(*courses)
		}
	}

	fun delete(vararg courses: Course) {
		GlobalScope.launch {
			groupDao.delete(*courses)
		}
	}

	fun getAll(): LiveData<List<Course>?> {
		val callback = MutableLiveData<List<Course>?>()
		serviceGroup.getAll().observe {
			callback.postValue(it?.sorted())
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(groupDao.getAll().sorted())
		}
		return callback
	}

	fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int): LiveData<Course?> {
		val callback = MutableLiveData<Course?>()
		serviceGroup.getByIdAndGroupNumber(courseUnitId, groupNumber).observe {
			callback.postValue(it ?: return@observe)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue(groupDao.getByIdAndGroupNumber(courseUnitId, groupNumber))
		}
		return callback
	}
}
