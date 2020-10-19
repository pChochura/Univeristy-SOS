package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.services.ServiceUSOSArticle
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryArticle(context: Context) {

	private val articleDao = AppDatabase.init(context).articleDao()
	private val serviceArticle = ServiceUSOSArticle.init()

	private fun insert(vararg articles: Article) {
		GlobalScope.launch {
			articleDao.insert(*articles)
		}
	}

	fun getAll() = ObserverWrapper<List<Article>> {
		postValue { articleDao.getAll() }
		postValue(SourceType.ONLINE) {
			serviceArticle.getAll().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getAllCategories() = ObserverWrapper<List<Article.Category>> {
		postValue {
			listOf(
				*articleDao.getAllCategories().filterNot { category ->
					category.name?.toString().isNullOrBlank()
				}.toTypedArray(),
				Article.Category("default", Name("Bez kategorii", "No category"))
			)
		}
		postValue(SourceType.ONLINE) { serviceArticle.getAllCategories() }
	}
}
