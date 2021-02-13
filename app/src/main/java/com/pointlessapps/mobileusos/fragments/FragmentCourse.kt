package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterUser
import com.pointlessapps.mobileusos.databinding.FragmentCourseBinding
import com.pointlessapps.mobileusos.databinding.TableItemCellBinding
import com.pointlessapps.mobileusos.databinding.TableItemRowBinding
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import java.util.*
import java.util.concurrent.CountDownLatch

@Keep
class FragmentCourse(id: String) :
	FragmentCoreImpl<FragmentCourseBinding>(FragmentCourseBinding::class.java), FragmentPinnable {

	constructor(course: Course) : this("${course.courseUnitId}#${course.groupNumber}")

	private val courseUnitId = id.split("#").first()
	private val groupNumber = id.split("#").last().toInt()

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getShortcut(fragment: FragmentCoreImpl<*>, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_courses to fragment.getString(R.string.loading))
		ViewModelProvider(fragment).get(ViewModelUser::class.java)
			.getGroupByIdAndGroupNumber(courseUnitId, groupNumber)
			.onOnceCallback { (course) ->
				if (course !== null) {
					GlobalScope.launch(Dispatchers.Main) {
						callback(R.drawable.ic_courses to course.courseName.toString())
					}

					return@onOnceCallback
				}
			}
	}

	override fun created() {
		prepareInstructorsList()
		prepareMeetingsList()
		prepareParticipantsList()

		refreshed()
		prepareClickListeners()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		if (isPinned(javaClass.name, "${courseUnitId}#${groupNumber}")) {
			binding().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		binding().horizontalProgressBar.isRefreshing = true
		prepareData {
			binding().pullRefresh.isRefreshing = false
			binding().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		setCollapsible(binding().buttonDescription, binding().courseDescription)
		setCollapsible(binding().buttonAssessmentCriteria, binding().courseAssessmentCriteria)
		setCollapsible(binding().buttonLearningOutcomes, binding().courseLearningOutcomes)

		binding().buttonPin.setOnClickListener {
			binding().buttonPin.setIconResource(
				if (togglePin(javaClass.name, "${courseUnitId}#${groupNumber}"))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun setCollapsible(button: MaterialButton, view: View) {
		button.setOnClickListener {
			view.isVisible.also {
				view.isVisible = !it
				button.setIconResource(
					if (it) {
						R.drawable.ic_arrow_down
					} else {
						R.drawable.ic_arrow_up
					}
				)
				TransitionManager.beginDelayedTransition(binding().root, AutoTransition())
			}
		}
	}

	private fun prepareCourseData(course: Course) {
		binding().courseName.text = course.courseName.toString()
		binding().courseInfo.text =
			getString(
				R.string.course_info,
				course.classType.toString().capitalize(Locale.getDefault()),
				course.groupNumber
			)
		binding().courseDescription.text =
			course.courseDescription.toString().replace("&nbsp;", " ")

		val layoutInflater = LayoutInflater.from(requireContext())
		Jsoup.parse(course.courseAssessmentCriteria.toString()).also { html ->
			binding().courseAssessmentCriteria.removeAllViews()
			html.select("table").firstOrNull()?.also { table ->
				table.select("tr").forEach {
					val row = TableItemRowBinding.inflate(layoutInflater).root
					it.select("td").map {
						row.addView(
							(TableItemCellBinding.inflate(layoutInflater, row, false).root).apply {
								text = it.ownText().capitalize(Locale.getDefault())
							})
					}
					binding().courseAssessmentCriteria.addView(row)
				}
			}
		}
		Jsoup.parse(course.courseLearningOutcomes.toString()).also { html ->
			binding().courseLearningOutcomes.removeAllViews()
			html.select("table").firstOrNull()?.also { table ->
				table.select("tr").forEach {
					val row =TableItemRowBinding.inflate(layoutInflater).root
					it.select("td").map {
						row.addView(
							(TableItemCellBinding.inflate(layoutInflater, row, false).root).apply {
								text = it.ownText().capitalize(Locale.getDefault())
							})
					}
					binding().courseLearningOutcomes.addView(row)
				}
			}
		}
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		val loaded = CountDownLatch(3)

		viewModelTimetable.getBytUnitIdAndGroupNumber(courseUnitId, groupNumber)
			.observe(this) { (list) ->
				(binding().listMeetings.adapter as? AdapterMeeting)?.update(list)
			}.onFinished { loaded.countDown() }

		viewModelUser.getGroupByIdAndGroupNumber(courseUnitId, groupNumber)
			.observe(this) { (course) ->
				if (course == null) {
					return@observe
				}

				course.lecturers?.map { it.id }?.also {
					viewModelUser.getUsersByIds(it)
						.observe(this) { (users) ->
							(binding().listInstructors.adapter as? AdapterUser)?.update(users)
							binding().listInstructors.setEmptyText(getString(R.string.no_instructors))
						}.onFinished { loaded.countDown() }
				}

				(binding().listParticipants.adapter as? AdapterUser)?.update(
					course.participants ?: return@observe
				)

				binding().courseParticipantsAmount.text =
					(course.participants?.count() ?: 0).toString()
				prepareCourseData(course)
			}.onFinished { loaded.countDown() }

		doAsync {
			loaded.await()
			GlobalScope.launch(Dispatchers.Main) {
				callback?.invoke()
			}
		}
	}

	private fun prepareInstructorsList() {
		binding().listInstructors.setAdapter(AdapterUser().apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentUser(it.id))
			}
		})
	}

	private fun prepareMeetingsList() {
		binding().listMeetings.setAdapter(AdapterMeeting().apply {
			onClickListener =
				{ Utils.showCourseInfo(requireContext(), it, viewModelUser, onChangeFragment) }
		})
		binding().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
	}

	private fun prepareParticipantsList() {
		binding().listParticipants.setAdapter(AdapterUser())
	}
}
