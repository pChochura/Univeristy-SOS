package com.pointlessapps.mobileusos.repositories

import android.content.Context
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.services.ServiceUSOSSurvey
import com.pointlessapps.mobileusos.utils.ObserverWrapper
import com.pointlessapps.mobileusos.utils.SourceType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RepositorySurvey(context: Context) {

	private val surveyDao = AppDatabase.init(context).surveyDao()
	private val serviceSurvey = ServiceUSOSSurvey.init()

	private fun insert(vararg surveys: Survey) {
		GlobalScope.launch {
			surveyDao.insert(*surveys)
		}
	}

	fun getToFill() = ObserverWrapper<List<Survey>> {
		postValue { surveyDao.getToFill() }
		postValue(SourceType.ONLINE) {
			serviceSurvey.getToFill().also {
				insert(*it.toTypedArray())
			}
		}
	}

	fun getById(id: String) = ObserverWrapper<Survey?> {
		postValue { surveyDao.getById(id) }
		postValue(SourceType.ONLINE) {
			serviceSurvey.getById(id)?.run {
				this.questions?.forEach { question ->
					if (question.level == 0) {
						question.subQuestions =
							getSubQuestions(
								this.questions,
								question.number,
								question.level + 1
							)?.apply {
								forEach { subQuestion ->
									subQuestion.subQuestions = getSubQuestions(
										this@run.questions,
										subQuestion.number,
										subQuestion.level + 1
									)
								}
							}
					}
				}
				this.numberOfAnswers =
					this.questions?.filterNot { it.possibleAnswers.isNullOrEmpty() }?.size ?: 0
				this.questions = this.questions?.filter { it.level == 0 }
				insert(this)
				return@run this
			}
		}
	}

	fun fillOutSurvey(id: String, answers: Map<String, String>, comment: String?) =
		ObserverWrapper<Map<String, Any>?> {
			postValue(SourceType.ONLINE) {
				serviceSurvey.fillOut(id, answers, comment)
			}
		}

	private fun getSubQuestions(questions: List<Survey.Question>?, number: String, level: Int) =
		questions?.filter {
			it.level == level && it.number.startsWith(number)
		}
}
