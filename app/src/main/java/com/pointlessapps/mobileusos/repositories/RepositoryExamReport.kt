package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.ExamReport
import com.pointlessapps.mobileusos.services.ServiceUSOSExamReport
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryExamReport(context: Context) {

	private val examReportDao = AppDatabase.init(context).examReportDao()
	private val serviceExamReport = ServiceUSOSExamReport.init()

	private fun insert(vararg examReports: ExamReport) {
		GlobalScope.launch {
			examReportDao.insert(*examReports)
		}
	}

	fun getById(examId: String) = ObserverWrapper<ExamReport?> {
		postValue(SourceType.ONLINE) {
			serviceExamReport.getById(examId).also {
				insert(it ?: return@also)
			}
		}
		postValue { examReportDao.getById(examId) }
	}
}
