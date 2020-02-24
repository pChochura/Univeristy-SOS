package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.services.ServiceUSOSGroup
import com.pointlessapps.mobileusos.utils.CombinedLiveData
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

	@Suppress("UNCHECKED_CAST")
	fun getAll(): LiveData<List<Group>> = CombinedLiveData(
		serviceGroup.getAll(),
		groupDao.getAll()
	) { onlineData, dbData ->
		onlineData ?: dbData
	} as LiveData<List<Group>>
}
