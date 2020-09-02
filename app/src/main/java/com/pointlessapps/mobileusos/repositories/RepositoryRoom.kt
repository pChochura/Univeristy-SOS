package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.services.ServiceUSOSRoom
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryRoom(application: Application) {

	private val roomDao = AppDatabase.init(application).roomDao()
	private val serviceRoom = ServiceUSOSRoom.init()

	private fun insert(vararg buildingRoom: BuildingRoom) {
		GlobalScope.launch {
			roomDao.insert(*buildingRoom)
		}
	}

	fun getById(roomId: String) = ObserverWrapper<BuildingRoom?> {
		postValue { roomDao.getById(roomId) }
		postValue(SourceType.ONLINE) {
			serviceRoom.getById(roomId).also {
				insert(it ?: return@also)
			}
		}
	}
}
