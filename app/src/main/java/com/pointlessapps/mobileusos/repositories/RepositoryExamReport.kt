package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.ExamReport
import com.pointlessapps.mobileusos.services.ServiceUSOSExamReport
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryExamReport(application: Application) {

	private val examReportDao = AppDatabase.init(application).examReportDao()
	private val serviceExamReport = ServiceUSOSExamReport.init()

	fun insert(vararg examReports: ExamReport) {
		GlobalScope.launch {
			examReportDao.insert(*examReports)
		}
	}

	fun update(vararg examReports: ExamReport) {
		GlobalScope.launch {
			examReportDao.update(*examReports)
		}
	}

	fun delete(vararg examReports: ExamReport) {
		GlobalScope.launch {
			examReportDao.delete(*examReports)
		}
	}

	fun getById(examId: String): LiveData<ExamReport> {
		val callback = MutableLiveData<ExamReport>()
		serviceExamReport.getById(examId).observe {
			callback.postValue(it ?: return@observe)
			insert(it)
		}
		GlobalScope.launch {
			callback.postValue(examReportDao.getById(examId) ?: return@launch)
		}
		return callback
	}
}
