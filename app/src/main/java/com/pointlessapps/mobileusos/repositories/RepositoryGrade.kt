package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.services.ServiceUSOSGrade
import com.pointlessapps.mobileusos.services.ServiceUSOSGroup
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGrade(application: Application) {

	private val gradeDao = AppDatabase.init(application).gradeDao()
	private val serviceGrade = ServiceUSOSGrade.init()

	fun insert(vararg grades: Grade) {
		GlobalScope.launch {
			gradeDao.insert(*grades)
		}
	}

	fun update(vararg grades: Grade) {
		GlobalScope.launch {
			gradeDao.update(*grades)
		}
	}

	fun delete(vararg grades: Grade) {
		GlobalScope.launch {
			gradeDao.delete(*grades)
		}
	}

	fun getByGroups(groups: List<Group>): LiveData<List<Grade>?> {
		val callback = MutableLiveData<List<Grade>?>()
		serviceGrade.getByGroups(groups).observe {
			callback.postValue(it)
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(gradeDao.getByGroups(groups))
		}
		return callback
	}
}
