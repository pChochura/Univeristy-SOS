package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterUser
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_course.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentCourse(id: String) : FragmentBase(), FragmentPinnable {

	constructor(course: Course) : this("${course.courseUnitId}#${course.groupNumber}")

	private val courseUnitId = id.split("#").first()
	private val groupNumber = id.split("#").last().toInt()

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_course

	override fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) {
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

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		if (isPinned(javaClass.name, "${courseUnitId}#${groupNumber}")) {
			root().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		root().horizontalProgressBar.isRefreshing = true
		prepareData {
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		setCollapsible(root().buttonDescription, root().courseDescription)
		setCollapsible(root().buttonAssessmentCriteria, root().courseAssessmentCriteria)
		setCollapsible(root().buttonLearningOutcomes, root().courseLearningOutcomes)

		root().buttonPin.setOnClickListener {
			root().buttonPin.setIconResource(
				if (togglePin(javaClass.name, "${courseUnitId}#${groupNumber}"))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun setCollapsible(button: MaterialButton, view: AppCompatTextView) {
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
				TransitionManager.beginDelayedTransition(root(), AutoTransition())
			}
		}
	}

	private fun prepareCourseData(course: Course) {
		root().courseName.text = course.courseName.toString()
		root().courseInfo.text =
			getString(
				R.string.course_info,
				course.classType.toString().capitalize(Locale.getDefault()),
				course.groupNumber
			)
		root().courseDescription.text = Utils.stripHtmlTags(course.courseDescription.toString())
		root().courseAssessmentCriteria.text =
			Utils.stripHtmlTags(course.courseAssessmentCriteria.toString())
		root().courseLearningOutcomes.text =
			Utils.stripHtmlTags(course.courseLearningOutcomes.toString())
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		val loaded = CountDownLatch(3)

		viewModelTimetable.getBytUnitIdAndGroupNumber(courseUnitId, groupNumber)
			.observe(this) { (list) ->
				(root().listMeetings.adapter as? AdapterMeeting)?.update(list)
			}.onFinished { loaded.countDown() }

		viewModelUser.getGroupByIdAndGroupNumber(courseUnitId, groupNumber)
			.observe(this) { (course) ->
				if (course == null) {
					return@observe
				}

				course.lecturers?.map { it.id }?.also {
					viewModelUser.getUsersByIds(it)
						.observe(this) { (users) ->
							(root().listInstructors.adapter as? AdapterUser)?.update(users)
							root().listInstructors.setEmptyText(getString(R.string.no_instructors))
						}.onFinished { loaded.countDown() }
				}

				(root().listParticipants.adapter as? AdapterUser)?.update(
					course.participants ?: return@observe
				)

				root().courseParticipantsAmount.text =
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
		root().listInstructors.setAdapter(AdapterUser().apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentUser(it.id))
			}
		})
	}

	private fun prepareMeetingsList() {
		root().listMeetings.setAdapter(AdapterMeeting().apply {
			onAddToCalendarClickListener = { Utils.calendarIntent(requireContext(), it) }

			onRoomClickListener = {
				it.roomId?.also { roomId -> onChangeFragment?.invoke(FragmentRoom(roomId)) }
			}
		})
		root().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
	}

	private fun prepareParticipantsList() {
		root().listParticipants.setAdapter(AdapterUser())
	}
}
