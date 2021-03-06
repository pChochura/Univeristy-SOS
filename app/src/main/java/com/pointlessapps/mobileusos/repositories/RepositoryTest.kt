package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.services.ServiceUSOSTest
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryTest(context: Context) {

	private val testDao = AppDatabase.init(context).testDao()
	private val testNodeDao = AppDatabase.init(context).testNodeDao()
	private val serviceTest = ServiceUSOSTest.init()

	private fun insertTests(vararg tests: Test) {
		GlobalScope.launch {
			testDao.insert(*tests)
		}
	}

	private fun insertNodes(vararg nodes: Test.Node) {
		GlobalScope.launch {
			testNodeDao.insert(*nodes)
		}
	}

	fun getAll() = ObserverWrapper<List<Test>> {
		postValue { testDao.getAll() }
		postValue(SourceType.ONLINE) {
			serviceTest.getAll().also {
				insertTests(*it.toTypedArray())
			}
		}
	}

	fun getNodesByIds(ids: List<String>) = ObserverWrapper<List<Test.Node>> {
		postValue {
			testNodeDao.getByIds(ids).apply {
				forEach {
					it.subNodes = testNodeDao.getByIds(it.subNodes?.map(Test.Node::id) ?: listOf())
						.toMutableList()
				}
			}
		}
		postValue(SourceType.ONLINE, 30) {
			mutableListOf<Test.Node>().apply {
				ids.forEach { id -> serviceTest.getNodeById(id)?.also { add(it) } }
				insertNodes(*this.toTypedArray())
			}
		}
	}
}
