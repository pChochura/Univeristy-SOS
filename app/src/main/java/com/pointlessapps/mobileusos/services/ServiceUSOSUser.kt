package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSUser private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getById(id: String?) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userDetailsRequest(id))?.run {
					gson.fromJson<User>(body)
				}
			}
		}

	suspend fun getByIds(ids: List<String>) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(usersRequest(ids))?.run {
					gson.fromJson<Map<String, User>>(body).values.toList()
				}
			}!!
		}

	suspend fun getByQuery(query: String) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(usersRequest(query))?.run {
					gson.fromJson<ResponseUserByQuery>(body).items?.flatMap { it.values }
				}
			}!!
		}

	suspend fun getCoursesByIds(ids: List<String>) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userCoursesRequest(ids))?.run {
					gson.fromJson<Map<String, Course>>(body).values.toList()
				}
			}!!
		}

	@Keep
	class ResponseUserByQuery {
		@SerializedName("items")
		var items: List<Map<String, User>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSUser, Unit>({ ServiceUSOSUser() })
}
