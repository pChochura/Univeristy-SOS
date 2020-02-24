package com.pointlessapps.mobileusos.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSTerm private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByIds(ids: List<String>): LiveData<List<Term>> {
		val terms = MutableLiveData<List<Term>>()
		doAsync {
			terms.postValue(
				clientService.run {
					execute(termsRequest(ids))?.run {
						gson.fromJson<Map<String, Term>>(body).values.toList().sorted()
					}
				}
			)
		}
		return terms
	}

	companion object : Utils.SingletonHolder<ServiceUSOSTerm, Unit>({ ServiceUSOSTerm() })
}