package com.pointlessapps.mobileusos.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.services.ServiceUSOSGrade
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositoryGrade(application: Application) {

	private val gradeDao = AppDatabase.init(application).gradeDao()
	private val serviceGrade = ServiceUSOSGrade.init()

	fun insert(vararg grades: Grade?) {
		GlobalScope.launch {
			gradeDao.insert(*(grades.filterNotNull().toTypedArray()))
		}
	}

	fun update(vararg grades: Grade) {
		GlobalScope.launch {
			gradeDao.update(*grades)
		}
	}

	fun delete(vararg grades: Grade) {
		GlobalScope.launch {
			gradeDao.delete(*grades)
		}
	}

	fun getByGroups(courses: List<Course>): LiveData<List<Grade>?> {
		val callback = MutableLiveData<List<Grade>?>()
		serviceGrade.getByGroups(courses).observe {
			callback.postValue(it)
			insert(*it?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(gradeDao.getByGroups(courses))
		}
		return callback
	}

	fun getByTermIds(termIds: List<String>): LiveData<Map<String, Map<String, Grade?>>?> {
		val callback = MutableLiveData<Map<String, Map<String, Grade?>>?>()
		serviceGrade.getByTermIds(termIds).observe { map ->
			callback.postValue(map)
			insert(*map?.values?.flatMap { it.values }?.toTypedArray() ?: return@observe)
		}
		GlobalScope.launch {
			callback.postValue(gradeDao.getByTermIds(termIds))
		}
		return callback
	}

	fun getRecentGrades(): LiveData<List<Grade>?> {
		val callback = MutableLiveData<List<Grade>?>()
		serviceGrade.getRecentGrades().observe { list ->
			callback.postValue(list)
			insert(*list?.toTypedArray() ?: return@observe)
		}
		return callback
	}
}
