package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.repositories.RepositoryArticle
import com.pointlessapps.mobileusos.repositories.RepositoryBuilding
import com.pointlessapps.mobileusos.repositories.RepositoryRoom
import com.pointlessapps.mobileusos.repositories.RepositoryUniversity

class ViewModelCommon(application: Application) : AndroidViewModel(application) {

	private val repositoryUniversity = RepositoryUniversity(application)
	private val repositoryNews = RepositoryArticle(application)
	private val repositoryRoom = RepositoryRoom(application)
	private val repositoryBuilding = RepositoryBuilding(application)

	fun getAllUniversities() = repositoryUniversity.getAll()

	fun getAllNews() = repositoryNews.getAll()

	fun getAllNewsCategories() = repositoryNews.getAllCategories()

	fun getRoomById(roomId: String) = repositoryRoom.getById(roomId)

	fun getBuildingById(buildingId: String) = repositoryBuilding.getById(buildingId)
}
