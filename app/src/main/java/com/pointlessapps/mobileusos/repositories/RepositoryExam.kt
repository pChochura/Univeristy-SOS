package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Exam
import com.pointlessapps.mobileusos.services.ServiceUSOSExam
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryExam(application: Application) {

	private val examDao = AppDatabase.init(application).examDao()
	private val serviceExam = ServiceUSOSExam.init()

	fun insert(vararg exams: Exam) {
		GlobalScope.launch {
			examDao.insert(*exams)
		}
	}

	fun update(vararg exams: Exam) {
		GlobalScope.launch {
			examDao.update(*exams)
		}
	}

	fun delete(vararg exams: Exam) {
		GlobalScope.launch {
			examDao.delete(*exams)
		}
	}

	fun getByIds(examIds: List<String>): LiveData<List<Exam>> {
		val callback = MutableLiveData<List<Exam>>()
		serviceExam.getByIds(examIds).observe {
			callback.postValue(it ?: return@observe)
			insert(*it.toTypedArray())
		}
		GlobalScope.launch {
			callback.postValue(examDao.getByIds(examIds))
		}
		return callback
	}
}
