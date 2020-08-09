package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSUser private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getById(id: String?): Callback<User?> {
		val callback = Callback<User?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userDetailsRequest(id))?.run {
						gson.fromJson<User>(body)
					}
				}
			)
		}
		return callback
	}

	fun getByIds(ids: List<String>): Callback<List<User>?> {
		val callback = Callback<List<User>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(usersRequest(ids))?.run {
						gson.fromJson<Map<String, User>>(body).values.toList()
					}
				}
			)
		}
		return callback
	}

	fun getByQuery(query: String): Callback<List<User>?> {
		val callback = Callback<List<User>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(usersRequest(query))?.run {
						gson.fromJson<ResponseUserByQuery>(body).items?.flatMap { it.values }
					}
				}
			)
		}
		return callback
	}

	fun getAllFaculties(): Callback<List<String>?> {
		val callback = Callback<List<String>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userCoursesRequest())?.run {
						gson.fromJson<ResponseFacultiesByQuery>(body).courseEditions?.values?.flatMap {
							it.flatMap { temp ->
								temp.userGroups?.map { temp2 ->
									temp2.courseFacId ?: ""
								} ?: listOf()
							}
						}
					}
				}
			)
		}
		return callback
	}

	fun getCoursesByIds(ids: List<String>): Callback<List<Course>?> {
		val callback = Callback<List<Course>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(userCoursesRequest(ids))?.run {
						gson.fromJson<Map<String, Course>>(body).values.toList()
					}
				}
			)
		}
		return callback
	}

	class ResponseUserByQuery {
		var items: List<Map<String, User>>? = null
	}

	class ResponseFacultiesByQuery {
		var courseEditions: Map<String, List<Temp>>? = null

		class Temp {
			var courseId: String? = null
			var userGroups: List<Temp2>? = null

			class Temp2 {
				var courseFacId: String? = null
			}
		}
	}

	companion object : Utils.SingletonHolder<ServiceUSOSUser, Unit>({ ServiceUSOSUser() })
}
