package com.pointlessapps.mobileusos.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import org.jetbrains.anko.doAsync

class ServiceUSOSUser private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(id: String?): LiveData<User?> {
		val user = MutableLiveData<User?>()
		doAsync {
			user.postValue(
				clientService.run {
					execute(userDetailsRequest(id))?.run {
						gson.fromJson(body, User::class.java)
					}
				}
			)
		}
		return user
	}

	companion object : Utils.SingletonHolder<ServiceUSOSUser, Unit>({ ServiceUSOSUser() })
}