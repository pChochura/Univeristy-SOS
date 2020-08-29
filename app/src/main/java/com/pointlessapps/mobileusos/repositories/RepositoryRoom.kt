package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.services.ServiceUSOSRoom
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryRoom(application: Application) {

	private val roomDao = AppDatabase.init(application).roomDao()
	private val serviceRoom = ServiceUSOSRoom.init()

	fun insert(vararg buildingRoom: BuildingRoom) {
		GlobalScope.launch {
			roomDao.insert(*buildingRoom)
		}
	}

	fun update(vararg buildingRoom: BuildingRoom) {
		GlobalScope.launch {
			roomDao.update(*buildingRoom)
		}
	}

	fun delete(vararg buildingRoom: BuildingRoom) {
		GlobalScope.launch {
			roomDao.delete(*buildingRoom)
		}
	}

	fun getById(roomId: String): LiveData<Pair<BuildingRoom, Boolean>> {
		val callback = MutableLiveData<Pair<BuildingRoom, Boolean>>()
		serviceRoom.getById(roomId).observe {
			callback.postValue((it ?: return@observe) to true)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue((roomDao.getById(roomId) ?: return@launch) to false)
		}
		return callback
	}
}
