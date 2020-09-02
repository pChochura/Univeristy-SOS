package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSGroup private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getAll() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userGroupsRequest())?.run {
					gson.fromJson<ResponseGroups>(body).groups?.values?.flatten()
				}
			}!!
		}

	suspend fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userGroupRequest(courseUnitId, groupNumber))?.run {
					gson.fromJson<Course>(body)
				}
			}
		}

	@Keep
	private class ResponseGroups {
		@SerializedName("groups")
		var groups: Map<String, List<Course>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGroup, Unit>({ ServiceUSOSGroup() })
}
