package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.services.ServiceUSOSArticle
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryArticle(application: Application) {

	private val articleDao = AppDatabase.init(application).articleDao()
	private val serviceArticle = ServiceUSOSArticle.init()

	fun insert(vararg articles: Article) {
		GlobalScope.launch {
			articleDao.insert(*articles)
		}
	}

	fun update(vararg articles: Article) {
		GlobalScope.launch {
			articleDao.update(*articles)
		}
	}

	fun delete(vararg articles: Article) {
		GlobalScope.launch {
			articleDao.delete(*articles)
		}
	}

	fun getAll(): LiveData<Pair<List<Article>, Boolean>> {
		val callback = MutableLiveData<Pair<List<Article>, Boolean>>()
		serviceArticle.getAll().observe {
			callback.postValue((it ?: return@observe) to true)
			insert(*it.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(articleDao.getAll() to false)
		}
		return callback
	}

	fun getAllCategories(): LiveData<List<Article.Category>> {
		val callback = MutableLiveData<List<Article.Category>>()
		serviceArticle.getAllCategories().observe {
			callback.postValue(it?.filterNot { category ->
				category.name?.toString().isNullOrBlank()
			} ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(
				listOf(
					*articleDao.getAllCategories().filterNot { category ->
						category.name?.toString().isNullOrBlank()
					}.toTypedArray(),
					Article.Category("default", Name("Bez kategorii", "No category"))
				)
			)
		}
		return callback
	}
}
