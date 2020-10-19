package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.services.ServiceUSOSGuide
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGuide(context: Context) {

	private val chapterDao = AppDatabase.init(context).chapterDao()
	private val serviceGuide = ServiceUSOSGuide.init()

	private fun insert(vararg chapters: Chapter) {
		GlobalScope.launch { chapterDao.insert(*chapters) }
	}

	fun getWhole() =
		ObserverWrapper<List<Chapter>> {
			postValue { chapterDao.getAll() }
			postValue(SourceType.ONLINE) {
				serviceGuide.getWhole().also {
					insert(*it.toTypedArray())
				}
			}
		}
}
