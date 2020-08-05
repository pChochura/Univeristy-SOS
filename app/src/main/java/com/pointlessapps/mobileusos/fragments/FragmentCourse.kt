package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterUser
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_course.view.*

class FragmentCourse(private val course: Course) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_course

	override fun created() {
		prepareInstructorsList()
		prepareMeetingsList()
		prepareParticipantsList()

		prepareCourseData()
	}

	private fun prepareCourseData() {
		root().courseName.text = course.courseName.toString()
		root().courseInfo.text =
			getString(R.string.course_info, course.classType.toString().apply {
				first().toUpperCase()
			}, course.groupNumber)
	}

	private fun prepareInstructorsList() {
		root().listInstructors.setAdapter(AdapterUser().apply {
			update(course.lecturers ?: return@apply)
		})
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
			.observe(this) {
				(root().listParticipants.adapter as? AdapterUser)?.update(
					it?.participants ?: return@observe
				)

				root().courseParticipantsAmount.text = (it?.participants?.count() ?: 0).toString()
			}
	}
}
