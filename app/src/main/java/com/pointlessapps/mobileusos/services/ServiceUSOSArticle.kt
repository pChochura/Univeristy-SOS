package com.pointlessapps.mobileusos.services

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.pointlessapps.mobileusos.clients.ClientUSOSService
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ServiceUSOSArticle private constructor() {

	private val clientService = ClientUSOSService.init()

	suspend fun getAll() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(newsRequest())?.run {
					gson.fromJson<Response>(body).items?.flatMap { it.values }
				}
			}!!
		}

	suspend fun getAllCategories() =
		withContext(Dispatchers.IO) {
			clientService.run {
				execute(newsCategoriesRequest())?.run {
					gson.fromJson<List<Article.Category>>(body)
				}
			}!!
		}

	@Keep
	class Response {
		@SerializedName("items")
		var items: List<Map<String, Article>>? = null
	}

	companion object :
		Utils.SingletonHolder<ServiceUSOSArticle, Unit>({ ServiceUSOSArticle() })
}
