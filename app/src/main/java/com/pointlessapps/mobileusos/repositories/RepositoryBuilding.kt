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

	fun getById(buildingId: String): LiveData<Pair<Building, Boolean>> {
		val callback = MutableLiveData<Pair<Building, Boolean>>()
		serviceBuilding.getById(buildingId).observe {
			callback.postValue((it ?: return@observe) to true)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue((buildingDao.getById(buildingId) ?: return@launch) to false)
		}
		return callback
	}
}
