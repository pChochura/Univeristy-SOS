package com.pointlessapps.mobileusos.services

import android.util.Log
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSBuilding private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(buildingId: String): Callback<Building?> {
		val callback = Callback<Building?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(buildingRequest(buildingId))?.run {
						gson.fromJson<Building>(body.also {
							Log.d("LOG!", "buidlings: $it")
						})
					}
				}
			)
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSBuilding, Unit>({ ServiceUSOSBuilding() })
}
