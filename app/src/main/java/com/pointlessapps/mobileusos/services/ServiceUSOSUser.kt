package com.pointlessapps.mobileusos.services

import android.util.Log
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

	fun getByQuery(query: String): Callback<List<User>?> {
		val callback = Callback<List<User>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(usersRequest(query))?.run {
						gson.fromJson<Response>(body.also {
							Log.d("LOG!", "users: $it")
						}).items?.flatMap { it.values }
					}
				}
			)
		}
		return callback
	}

	class Response {
		var items: List<Map<String, User>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSUser, Unit>({ ServiceUSOSUser() })
}
