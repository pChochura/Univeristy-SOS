package com.pointlessapps.mobileusos.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import java.util.*

class ViewModelTimetable(application: Application) : AndroidViewModel(application) {

	private val repositoryTimetable = RepositoryTimetable(application)

	fun getByUser(
		startTime: Calendar,
		userId: String? = null,
		numberOfDays: Int = 7
	) = repositoryTimetable.getByUser(userId, startTime, numberOfDays)
}
