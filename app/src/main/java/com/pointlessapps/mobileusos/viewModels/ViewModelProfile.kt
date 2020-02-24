package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.repositories.RepositoryUser

class ViewModelProfile(application: Application) : AndroidViewModel(application) {

	private val repositoryUser = RepositoryUser(application)

	fun getUserById(id: String?): LiveData<User?> {
		return repositoryUser.getById(id)
	}
}
