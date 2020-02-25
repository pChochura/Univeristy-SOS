package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.services.ServiceUSOSTerm
import com.pointlessapps.mobileusos.utils.Callback
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

	fun getByIds(ids: List<String>): LiveData<List<Term>?> {
		val callback = MutableLiveData<List<Term>?>()
		serviceTerm.getByIds(ids).observe {
			callback.postValue(it?.sorted())
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(termDao.getByIds(ids).sorted())
		}
		return callback
	}
}
