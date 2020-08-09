package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterUser
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.capitalize
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_course.view.*

class FragmentCourse(private var course: Course) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_course

	override fun created() {
		prepareInstructorsList()
		prepareMeetingsList()
		prepareParticipantsList()

		prepareCourseData()
		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		setCollapsible(root().buttonDescription, root().courseDescription)
		setCollapsible(root().buttonAssessmentCriteria, root().courseAssessmentCriteria)
		setCollapsible(root().buttonLearningOutcomes, root().courseLearningOutcomes)
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

	private fun prepareCourseData() {
		root().courseName.text = course.courseName.toString()
		root().courseInfo.text =
			getString(
				R.string.course_info,
				course.classType.toString().capitalize(),
				course.groupNumber
			)
		root().courseDescription.text = Utils.stripHtmlTags(course.courseDescription.toString())
		root().courseAssessmentCriteria.text =
			Utils.stripHtmlTags(course.courseAssessmentCriteria.toString())
		root().courseLearningOutcomes.text =
			Utils.stripHtmlTags(course.courseLearningOutcomes.toString())
	}

	private fun prepareInstructorsList() {
		root().listInstructors.setAdapter(AdapterUser().apply {
			onClickListener = {
				onChangeFragmentListener?.invoke(FragmentUser(it.id))
			}
		})

		viewModelUser.getUserByIds(course.lecturers?.map { it.id } ?: return).observe(this) {
			(root().listInstructors.adapter as? AdapterUser)?.update(it ?: return@observe)
			root().listInstructors.setEmptyText(getString(R.string.no_instructors))
		}
	}

	private fun prepareMeetingsList() {
		root().listMeetings.setAdapter(AdapterMeeting().apply {
			onAddToCalendarClickListener = { Utils.calendarIntent(requireContext(), it) }

			onRoomClickListener = {
				onChangeFragmentListener?.invoke(FragmentRoom(it.roomNumber, it.roomId ?: ""))
			}
		})
		root().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))

		viewModelTimetable.getBytUnitIdAndGroupNumber(course.courseUnitId, course.groupNumber)
			.observe(this) {
				(root().listMeetings.adapter as? AdapterMeeting)?.update(it)
			}
	}

	private fun prepareParticipantsList() {
		root().listParticipants.setAdapter(AdapterUser().apply {
			update(course.participants ?: return@apply)
		})

		root().courseParticipantsAmount.text = (course.participants?.count() ?: 0).toString()

		viewModelUser.getGroupByIdAndGroupNumber(course.courseUnitId, course.groupNumber)
			.observe(this) { group ->
				course = group ?: return@observe

				(root().listParticipants.adapter as? AdapterUser)?.update(
					group.participants ?: return@observe
				)

				root().courseParticipantsAmount.text = (group.participants?.count() ?: 0).toString()
				prepareCourseData()
			}
	}
}
