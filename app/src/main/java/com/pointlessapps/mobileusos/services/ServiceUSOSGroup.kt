package com.pointlessapps.mobileusos.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSGroup private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getAll(): LiveData<List<Group>> {
		val groups = MutableLiveData<List<Group>>()
		doAsync {
			groups.postValue(
				clientService.run {
					execute(userGroupsRequest())?.run {
						gson.fromJson<ResponseGroups>(body).groups?.values?.flatten()
					}
				}
			)
		}
		return groups
	}

	private class ResponseGroups {
		internal var groups: Map<String, List<Group>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGroup, Unit>({ ServiceUSOSGroup() })
}