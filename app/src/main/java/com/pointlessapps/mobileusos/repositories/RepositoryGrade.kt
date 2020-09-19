package com.pointlessapps.mobileusos.repositories

import android.app.Application
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.services.ServiceUSOSGrade
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGrade(application: Application) {

	private val gradeDao = AppDatabase.init(application).gradeDao()
	private val serviceGrade = ServiceUSOSGrade.init()

	private fun insert(vararg grades: Grade?) {
		GlobalScope.launch {
			gradeDao.insert(*(grades.filterNotNull().toTypedArray()))
		}
	}

	fun getByTermIds(termIds: List<String>) = ObserverWrapper<Map<String, Map<String, Grade?>>> {
		postValue { gradeDao.getByTermIds(termIds) }
		postValue(SourceType.ONLINE) {
			serviceGrade.getByTermIds(termIds).also {
				insert(*it.values.flatMap(Map<String, Grade?>::values).toTypedArray())
			}
		}
	}

	fun getRecentGrades() = ObserverWrapper<List<Grade>> {
		postValue { gradeDao.getRecent() }
		postValue(SourceType.ONLINE) {
			serviceGrade.getRecentGrades().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getByExam(examId: String, examSessionNumber: Int) = ObserverWrapper<Grade?> {
		postValue { gradeDao.getByExam(examId, examSessionNumber) }
		postValue(SourceType.ONLINE) {
			serviceGrade.getByExam(examId, examSessionNumber)?.apply {
				courseName = courseEdition?.courseName
				insert(this)
			}
		}
	}
}
