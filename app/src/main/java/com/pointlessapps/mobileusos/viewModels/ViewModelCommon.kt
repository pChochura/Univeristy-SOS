package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.repositories.*
import java.util.*

class ViewModelCommon(application: Application) : AndroidViewModel(application) {

	private val repositoryUniversity = RepositoryUniversity(application)
	private val repositoryNews = RepositoryArticle(application)
	private val repositoryRoom = RepositoryRoom(application)
	private val repositoryBuilding = RepositoryBuilding(application)
	private val repositoryCalendar = RepositoryCalendar(application)

	fun getAllUniversities() = repositoryUniversity.getAll()

	fun getAllNews() = repositoryNews.getAll()

	fun getAllNewsCategories() = repositoryNews.getAllCategories()

	fun getRoomById(roomId: String) = repositoryRoom.getById(roomId)

	fun getBuildingById(buildingId: String) = repositoryBuilding.getById(buildingId)

	fun getCalendarByFaculties(faculties: List<String>, startDate: Date) =
		repositoryCalendar.getByFaculties(faculties, startDate, Calendar.getInstance().apply {
			timeInMillis = startDate.time
			add(Calendar.MONTH, 1)
		}.time)
}
