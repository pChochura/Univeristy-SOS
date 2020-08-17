package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSGrade private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getByGroups(courses: List<Course>): Callback<List<Grade>?> {
		val callback = Callback<List<Grade>?>()
		val data = mutableListOf<Grade>()
		doAsync {
			courses.forEach { group ->
				callback.post(
					data.apply {
						clientService.run {
							execute(userGradesRequest(group))?.run {
								gson.fromJson<ResponseCourseGrades>(body).courseGrades?.values?.firstOrNull()
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

	fun getByTermIds(termIds: List<String>): Callback<Map<String, Map<String, Grade?>>?> {
		val callback = Callback<Map<String, Map<String, Grade?>>?>()
		doAsync {
			callback.post(
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
					}
				}
			)
		}
		return callback
	}

	fun getRecentGrades(): Callback<List<Grade>?> {
		val callback = Callback<List<Grade>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userRecentGradesRequest())?.run {
						gson.fromJson<List<Grade>>(body)
					}
				}
			)
		}
		return callback
	}

	@Keep
	private class ResponseCourseGrades {
		@SerializedName("course_grades")
		internal var courseGrades: Map<String, Grade>? = null
	}

	@Keep
	private class ResponseCourseGradesList {
		@SerializedName("course_grades")
		internal var courseGrades: List<Map<String, Grade?>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGrade, Unit>({ ServiceUSOSGrade() })
}
