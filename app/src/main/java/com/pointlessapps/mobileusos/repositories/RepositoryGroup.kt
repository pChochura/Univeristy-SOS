package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.services.ServiceUSOSGroup
import com.pointlessapps.mobileusos.utils.Callback
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGroup(application: Application) {

	private val groupDao = AppDatabase.init(application).groupDao()
	private val serviceGroup = ServiceUSOSGroup.init()

	fun insert(vararg groups: Group) {
		GlobalScope.launch {
			groupDao.insert(*groups)
		}
	}

	fun update(vararg groups: Group) {
		GlobalScope.launch {
			groupDao.update(*groups)
		}
	}

	fun delete(vararg groups: Group) {
		GlobalScope.launch {
			groupDao.delete(*groups)
		}
	}

	fun getAll(): LiveData<List<Group>?> {
		val callback = MutableLiveData<List<Group>?>()
		serviceGroup.getAll().observe {
			callback.postValue(it?.sorted())
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(groupDao.getAll().sorted())
		}
		return callback
	}
}
