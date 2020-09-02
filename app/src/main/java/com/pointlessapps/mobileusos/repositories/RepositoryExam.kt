package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Exam
import com.pointlessapps.mobileusos.services.ServiceUSOSExam
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryExam(application: Application) {

	private val examDao = AppDatabase.init(application).examDao()
	private val serviceExam = ServiceUSOSExam.init()

	private fun insert(vararg exams: Exam) {
		GlobalScope.launch {
			examDao.insert(*exams)
		}
	}

	fun getByIds(examIds: List<String>) = ObserverWrapper<List<Exam>> {
		postValue { examDao.getByIds(examIds) }
		postValue(SourceType.ONLINE) {
			serviceExam.getByIds(examIds).also {
				insert(*it.toTypedArray())
			}
		}
	}
}
