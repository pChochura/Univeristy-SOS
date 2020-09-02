package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSGrade private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getByTermIds(termIds: List<String>) =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userGradesRequest(termIds))?.run {
					gson.fromJson<Map<String, Map<String, ResponseCourseGradesList>>>(body)
						.mapValues { entry ->
							entry.value.mapValues {
								it.value.courseGrades?.flatMap { courseGrades ->
									courseGrades.mapValues { grade ->
										grade.value?.courseId = it.key
										grade.value?.termId = entry.key
										grade.value
									}.values.toList()
								}?.get(0)
							}
						}
				}!!
			}
		}

	suspend fun getRecentGrades() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(userRecentGradesRequest())?.run {
					gson.fromJson<List<Grade>>(body)
				}
			}!!
		}

	@Keep
	private class ResponseCourseGradesList {
		@SerializedName("course_grades")
		var courseGrades: List<Map<String, Grade?>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGrade, Unit>({ ServiceUSOSGrade() })
}
