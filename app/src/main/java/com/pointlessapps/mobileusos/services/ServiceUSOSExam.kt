package com.pointlessapps.mobileusos.services

import android.util.Log
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Exam
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSExam private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByIds(ids: List<String>): Callback<List<Exam>?> {
		val callback = Callback<List<Exam>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(examRequest(ids))?.run {
						gson.fromJson<Map<String, Exam>>(body.also {
							Log.d("LOG!", "ids: $ids")
							Log.d("LOG!", "exams: $it")
						}).values.toList()
					}
				}
			)
		}
		return callback
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSExam, Unit>({ ServiceUSOSExam() })
}
