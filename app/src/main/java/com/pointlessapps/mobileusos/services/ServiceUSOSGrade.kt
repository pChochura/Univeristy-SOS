package com.pointlessapps.mobileusos.services

import android.util.Log
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
						gson.fromJson<Map<String, Map<String, ResponseCourseGradesList>>>(body.also {
							Log.d("LOG!", "grades: $it")
						})
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

	private class ResponseCourseGrades {
		internal var courseGrades: Map<String, Grade>? = null
	}

	private class ResponseCourseGradesList {
		internal var courseGrades: List<Map<String, Grade?>>? = null
	}

	companion object : Utils.SingletonHolder<ServiceUSOSGrade, Unit>({ ServiceUSOSGrade() })
}
