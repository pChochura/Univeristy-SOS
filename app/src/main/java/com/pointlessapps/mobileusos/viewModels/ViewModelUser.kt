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
	private val repositoryExam = RepositoryExam(application)
	private val repositoryEmail = RepositoryEmail(application)

	fun getUserById(id: String? = null) = repositoryUser.getById(id)

	fun getUsersByIds(ids: List<String>) = repositoryUser.getByIds(ids)

	fun getUsersByQuery(query: String) = repositoryUser.getByQuery(query)

	fun getCoursesByIds(ids: List<String>) = repositoryUser.getCoursesByIds(ids)

	fun getAllGroups() = repositoryGroup.getAll()

	fun getGroupByIdAndGroupNumber(courseUnitId: String, groupNumber: Int) =
		repositoryGroup.getByIdAndGroupNumber(courseUnitId, groupNumber)

	fun getTermsByIds(termIds: List<String>) =
		repositoryTerm.getByIds(termIds.distinct().takeIf { it.isNotEmpty() } ?: listOf())

	fun getGradesByTermIds(termIds: List<String>) = repositoryGrade.getByTermIds(termIds)

	fun getRecentGrades() = repositoryGrade.getRecentGrades()

	fun getExamReportById(examId: String) = repositoryExamReport.getById(examId)

	fun getAllEmails() = repositoryEmail.getAll()

	fun getEmailById(emailId: String) = repositoryEmail.getById(emailId)

	fun getEmailRecipients(emailId: String) = repositoryEmail.getRecipientsById(emailId)

	fun createEmail(subject: String, content: String) =
		repositoryEmail.create(subject, content)

	fun updateEmailRecipients(id: String, userIds: List<String>, emails: List<String>) =
		repositoryEmail.updateRecipients(id, userIds, emails)

	fun addEmailAttachment(id: String, data: ByteArray, filename: String) =
		repositoryEmail.addAttachment(id, data, filename)
}
