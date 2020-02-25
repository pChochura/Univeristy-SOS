package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSUser private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(id: String?): Callback<User?> {
		val callback = Callback<User?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userDetailsRequest(id))?.run {
						gson.fromJson<User>(body)
					}
				}
			)
		}
		return callback
	}

	companion object : Utils.SingletonHolder<ServiceUSOSUser, Unit>({ ServiceUSOSUser() })
}