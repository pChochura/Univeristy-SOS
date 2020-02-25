package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSGroup private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getAll(): Callback<List<Group>?> {
		val callback = Callback<List<Group>?>()
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

	private class ResponseGroups {
		internal var groups: Map<String, List<Group>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGroup, Unit>({ ServiceUSOSGroup() })
}