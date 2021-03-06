package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.services.ServiceUSOSBuilding
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryBuilding(context: Context) {

	private val buildingDao = AppDatabase.init(context).buildingDao()
	private val serviceBuilding = ServiceUSOSBuilding.init()

	private fun insert(vararg building: Building) {
		GlobalScope.launch {
			buildingDao.insert(*building)
		}
	}

	fun getById(buildingId: String) = ObserverWrapper<Building?> {
		postValue { buildingDao.getById(buildingId) }
		postValue(SourceType.ONLINE) {
			serviceBuilding.getById(buildingId).also {
				insert(it ?: return@also)
			}
		}
	}
}
