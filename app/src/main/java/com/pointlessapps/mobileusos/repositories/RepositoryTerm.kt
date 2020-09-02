package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.services.ServiceUSOSTerm
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryTerm(application: Application) {

	private val termDao = AppDatabase.init(application).termDao()
	private val serviceTerm = ServiceUSOSTerm.init()

	private fun insert(vararg terms: Term) {
		GlobalScope.launch {
			termDao.insert(*terms)
		}
	}

	fun getByIds(ids: List<String>) = ObserverWrapper<List<Term>> {
		postValue { termDao.getByIds(ids).sorted() }
		postValue(SourceType.ONLINE) {
			serviceTerm.getByIds(ids).also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getAll() = ObserverWrapper<List<Term>> {
		postValue { termDao.getAll().sorted() }
		postValue(SourceType.ONLINE) {
			serviceTerm.getAll().also {
				insert(*it.toTypedArray())
			}
		}
	}
}
