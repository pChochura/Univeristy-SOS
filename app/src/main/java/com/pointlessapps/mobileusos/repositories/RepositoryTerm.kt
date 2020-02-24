package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.services.ServiceUSOSTerm
import com.pointlessapps.mobileusos.utils.CombinedLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryTerm(application: Application) {

	private val termDao = AppDatabase.init(application).termDao()
	private val serviceTerm = ServiceUSOSTerm.init()

	fun insert(vararg terms: Term) {
		GlobalScope.launch {
			termDao.insert(*terms)
		}
	}

	fun update(vararg terms: Term) {
		GlobalScope.launch {
			termDao.update(*terms)
		}
	}

	fun delete(vararg terms: Term) {
		GlobalScope.launch {
			termDao.delete(*terms)
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun getByIds(ids: List<String>) = CombinedLiveData(
		serviceTerm.getByIds(ids),
		termDao.getByIds(ids)
	) { onlineData, dbData ->
		onlineData ?: dbData
	} as LiveData<List<Term>>
}
