package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.services.ServiceUSOSTest
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryTest(application: Application) {

	private val testDao = AppDatabase.init(application).testDao()
	private val serviceTest = ServiceUSOSTest.init()

	private fun insert(vararg tests: Test) {
		GlobalScope.launch {
			testDao.insert(*tests)
		}
	}

	fun getAll() = ObserverWrapper<List<Test>> {
		postValue { testDao.getAll() }
		postValue(SourceType.ONLINE) {
			serviceTest.getAll().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getResultsByNodeIds(ids: List<String>) = ObserverWrapper<List<Test.Node.ResultObject>> {
		postValue(SourceType.ONLINE) {
			listOf(
				*serviceTest.getGradesByNodeIds(ids).toTypedArray(),
				*serviceTest.getPointsByNodeIds(ids).toTypedArray()
			)
		}
	}

	fun getNodesByIds(ids: List<String>) = ObserverWrapper<List<Test.Node>> {
		postValue(SourceType.ONLINE) {
			mutableListOf<Test.Node>().apply {
				ids.forEach { id -> serviceTest.getNodeById(id)?.also { add(it) } }
			}
		}
	}
}
