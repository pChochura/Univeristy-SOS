package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSGrade private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByGroups(groups: List<Group>): Callback<List<Grade>?> {
		val callback = Callback<List<Grade>?>()
		val data = mutableListOf<Grade>()
		doAsync {
			groups.forEach { group ->
				callback.post(
					data.apply {
						clientService.run {
							execute(userGradesRequest(group))?.run {
								gson.fromJson<ResponseGrades>(body)
									.courseGrades?.values?.firstOrNull()
							}
						}?.also {
							it.courseId = group.courseId
							it.termId = group.termId
							add(it)
						}
					}
				)
			}
		}
		return callback
	}

	private class ResponseGrades {
		internal var courseGrades: Map<String, Grade?>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGrade, Unit>({ ServiceUSOSGrade() })
}