package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.repositories.*

class ViewModelUser(application: Application) : AndroidViewModel(application) {

	private val repositoryUser = RepositoryUser(application)
	private val repositoryTerm = RepositoryTerm(application)
	private val repositoryGroup = RepositoryGroup(application)
	private val repositoryGrade = RepositoryGrade(application)
	private val repositoryExamReport = RepositoryExamReport(application)
	private val repositoryTest = RepositoryTest(application)
	private val repositorySurvey = RepositorySurvey(application)
	private val repositoryEmail = RepositoryEmail(application)

	fun getUserById(id: String? = null) = repositoryUser.getById(id)

	fun getUsersByIds(ids: List<String>) = repositoryUser.getByIds(ids)

	fun getUsersByQuery(query: String) = repositoryUser.getByQuery(query)

	fun getAllGroups() = repositoryGroup.getAll()

	fun getGroupByIdAndGroupNumber(courseUnitId: String, groupNumber: Int) =
		repositoryGroup.getByIdAndGroupNumber(courseUnitId, groupNumber)

	fun getTermsByIds(termIds: List<String>) =
		repositoryTerm.getByIds(termIds.distinct().takeIf { it.isNotEmpty() } ?: listOf())

	fun getGradesByTermIds(termIds: List<String>) = repositoryGrade.getByTermIds(termIds)

	fun getRecentGrades() = repositoryGrade.getRecentGrades()

	fun getGradeByExam(examId: String, examSessionNumber: Int) =
		repositoryGrade.getByExam(examId, examSessionNumber)

	fun getExamReportById(examId: String) = repositoryExamReport.getById(examId)

	fun getAllTests() = repositoryTest.getAll()

	fun getTestNodesByIds(ids: List<String>) = repositoryTest.getNodesByIds(ids)

	fun getSurveysToFill() = repositorySurvey.getToFill()

	fun getSurveysById(id: String) = repositorySurvey.getById(id)

	fun fillOutSurvey(id: String, answers: Map<String, Map<String, Any?>>, comment: String?) =
		repositorySurvey.fillOutSurvey(
			id,
			answers,
			comment
		)

	fun getAllEmails() = repositoryEmail.getAll()

	fun getEmailById(emailId: String) = repositoryEmail.getById(emailId)

	fun getEmailRecipients(emailId: String) = repositoryEmail.getRecipientsById(emailId)

	fun deleteEmail(id: String) =
		repositoryEmail.delete(id)

	fun createEmail(subject: String, content: String) =
		repositoryEmail.create(subject, content)

	fun updateEmail(id: String, subject: String, content: String) =
		repositoryEmail.update(id, subject, content)

	fun sendEmail(id: String) =
		repositoryEmail.send(id)

	fun updateEmailRecipients(id: String, userIds: List<String>, emails: List<String>) =
		repositoryEmail.updateRecipients(id, userIds, emails)

	fun refreshEmailRecipients(id: String) =
		repositoryEmail.refreshRecipients(id)

	fun addEmailAttachment(id: String, data: ByteArray, filename: String) =
		repositoryEmail.addAttachment(id, data, filename)

	fun deleteEmailAttachment(id: String) =
		repositoryEmail.deleteAttachment(id)
}
