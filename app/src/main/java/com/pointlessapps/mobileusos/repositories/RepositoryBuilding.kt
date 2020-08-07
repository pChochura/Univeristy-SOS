package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.services.ServiceUSOSBuilding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryBuilding(application: Application) {

	private val buildingDao = AppDatabase.init(application).buildingDao()
	private val serviceBuilding = ServiceUSOSBuilding.init()

	fun insert(vararg building: Building) {
		GlobalScope.launch {
			buildingDao.insert(*building)
		}
	}

	fun update(vararg building: Building) {
		GlobalScope.launch {
			buildingDao.update(*building)
		}
	}

	fun delete(vararg building: Building) {
		GlobalScope.launch {
			buildingDao.delete(*building)
		}
	}

	fun getById(buildingId: String): LiveData<Building?> {
		val callback = MutableLiveData<Building>()
		serviceBuilding.getById(buildingId).observe {
			callback.postValue(it ?: return@observe)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue(buildingDao.getById(buildingId) ?: return@launch)
		}
		return callback
	}
}
