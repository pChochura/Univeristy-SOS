package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.services.ServiceUSOSGroup
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGroup(context: Context) {

	private val groupDao = AppDatabase.init(context).groupDao()
	private val serviceGroup = ServiceUSOSGroup.init()

	private fun insert(vararg courses: Course) {
		GlobalScope.launch {
			groupDao.insert(*courses)
		}
	}

	fun getAll() = ObserverWrapper<List<Course>> {
		postValue { groupDao.getAll().sorted() }
		postValue(SourceType.ONLINE) {
			serviceGroup.getAll().sorted().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getByIdAndGroupNumber(courseUnitId: String, groupNumber: Int) = ObserverWrapper<Course?> {
		postValue { groupDao.getByIdAndGroupNumber(courseUnitId, groupNumber) }
		postValue(SourceType.ONLINE) {
			serviceGroup.getByIdAndGroupNumber(courseUnitId, groupNumber).also {
				insert(it ?: return@also)
			}
		}
	}
}
