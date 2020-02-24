package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.repositories.RepositoryGroup
import com.pointlessapps.mobileusos.repositories.RepositoryTerm
import com.pointlessapps.mobileusos.repositories.RepositoryUser

class ViewModelProfile(application: Application) : AndroidViewModel(application) {

	private val repositoryUser = RepositoryUser(application)
	private val repositoryTerm = RepositoryTerm(application)
	private val repositoryGroup = RepositoryGroup(application)

	fun getUserById(id: String? = null) = repositoryUser.getById(id)

	fun getAllGroups() = repositoryGroup.getAll()

	fun getTermsByIds(termIds: List<String>) = repositoryTerm.getByIds(termIds)
}
