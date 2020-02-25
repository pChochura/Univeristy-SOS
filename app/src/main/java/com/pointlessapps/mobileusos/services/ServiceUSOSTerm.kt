package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSTerm private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByIds(ids: List<String>): Callback<List<Term>?> {
		val callback = Callback<List<Term>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(termsRequest(ids))?.run {
						gson.fromJson<Map<String, Term>>(body).values.toList().sorted()
					}
				}
			)
		}
		return callback
	}

	companion object : Utils.SingletonHolder<ServiceUSOSTerm, Unit>({ ServiceUSOSTerm() })
}