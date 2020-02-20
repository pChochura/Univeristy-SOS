package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.repositories.RepositoryUniversity

class ViewModelUniversity(application: Application) : AndroidViewModel(application) {

	private val repositoryUniversity = RepositoryUniversity(application)
	private val universities = repositoryUniversity.getAll()

	fun insert(vararg universities: University) = repositoryUniversity.insert(*universities)
	fun update(vararg universities: University) = repositoryUniversity.update(*universities)
	fun delete(vararg universities: University) = repositoryUniversity.delete(*universities)

	fun getAll() = universities
	fun getByName(name: String) = repositoryUniversity.getByName(name)
	fun getByLocation(location: String) = repositoryUniversity.getByLocation(location)
}