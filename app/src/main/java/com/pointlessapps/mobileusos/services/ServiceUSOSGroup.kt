package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSGroup private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getAll(): Callback<List<Course>?> {
		val callback = Callback<List<Course>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userGroupsRequest())?.run {
						gson.fromJson<ResponseGroups>(body).groups?.values?.flatten()
					}
				}
			)
		}
		return callback
	}

	fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int): Callback<Course?> {
		val callback = Callback<Course?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userGroupRequest(courseUnitId, groupNumber))?.run {
						gson.fromJson<Course>(body)
					}
				}
			)
		}
		return callback
	}

	@Keep
	private class ResponseGroups {
		@SerializedName("groups")
		internal var groups: Map<String, List<Course>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGroup, Unit>({ ServiceUSOSGroup() })
}
