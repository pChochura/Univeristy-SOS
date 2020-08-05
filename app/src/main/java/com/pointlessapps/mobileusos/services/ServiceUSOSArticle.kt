package com.pointlessapps.mobileusos.services

import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import org.jetbrains.anko.doAsync

class ServiceUSOSArticle private constructor() {

	private val clientService = ClientUSOSService.init()

	fun getAll(): Callback<List<Article>?> {
		val callback = Callback<List<Article>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(newsRequest())?.run {
						gson.fromJson<Response>(body).items?.flatMap { it.values }
					}
				}
			)
		}
		return callback
	}

	fun getAllCategories(): Callback<List<Article.Category>?> {
		val callback = Callback<List<Article.Category>?>()
		doAsync {
			callback.post(
				clientService.run {
					execute(newsCategoriesRequest())?.run {
						gson.fromJson<List<Article.Category>>(body)
					}
				}
			)
		}
		return callback
	}

	class Response {
		var items: List<Map<String, Article>>? = null
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSArticle, Unit>({ ServiceUSOSArticle() })
}
