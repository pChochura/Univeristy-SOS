package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterRecentGrade
import com.pointlessapps.mobileusos.adapters.AdapterTerm
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.partial_profile_shortcuts.view.*
import java.util.*

class FragmentProfile : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val currentTerm = MutableLiveData<String?>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_profile
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareTermsList()
		prepareGradesList()
		prepareMeetingsList()
		prepareProfileData()
		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		root().buttonGrades.setOnClickListener { onChangeFragmentListener?.invoke(FragmentGrades()) }
		root().buttonCourses.setOnClickListener { onChangeFragmentListener?.invoke(FragmentCourses()) }
		root().buttonSurveys.setOnClickListener { onChangeFragmentListener?.invoke(FragmentSurveys()) }
		root().buttonSettings.setOnClickListener { onChangeFragmentListener?.invoke(FragmentSettings()) }
	}

	private fun prepareGradesList() {
		root().listRecentGrades.apply {
			setAdapter(AdapterRecentGrade().apply {
				onClickListener = {
					FragmentGrades.showGradeDialog(this@FragmentProfile, it, viewModelUser)
				}
			})
			setLayoutManager(
				LinearLayoutManager(
					requireContext(),
					RecyclerView.HORIZONTAL,
					false
				)
			)
		}

		val dateToCheck = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -7) }.time

		currentTerm.observe(this) { term ->
			if (term == null) {
				return@observe
			}

			viewModelUser.getGradesByTermIds(listOf(term)).observe(this) { grades ->
				root().listRecentGrades.setEmptyText(getString(R.string.no_recent_grades))

				var gradesList =
					grades?.toList()?.firstOrNull()?.second?.values?.toList() ?: return@observe
				gradesList =
					gradesList.filter { (it?.dateModified?.compareTo(dateToCheck) ?: 1) > 0 }
						.filterNotNull()

				viewModelUser.getCoursesByIds(gradesList.map { grade -> grade.courseId })
					.observe(this) {
						val courses = it?.associateBy { course -> course.courseId }
						gradesList.forEach { grade ->
							grade.courseName = courses?.get(grade.courseId)?.courseName
						}

						(root().listRecentGrades.adapter as? AdapterRecentGrade)?.apply {
							update(gradesList.sortedBy { grade ->
								grade.dateModified
							})
						}
					}
			}
		}
	}

	private fun prepareMeetingsList() {
		root().listMeetings.apply {
			setAdapter(AdapterMeeting(true).apply {
				onAddToCalendarClickListener = {
					Utils.calendarIntent(requireContext(), it)
				}
				onRoomClickListener = {
					it.roomId?.apply {
						onChangeFragmentListener?.invoke(FragmentRoom(it.roomNumber, this))
					}
				}
			})
		}

		viewModelTimetable.getIncoming().observe(this) {
			(root().listMeetings.adapter as? AdapterMeeting)?.update(it)

			root().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
		}
	}

	private fun prepareTermsList() {
		root().listTerms.setAdapter(AdapterTerm())

		viewModelUser.getAllGroups().observe(this) { (terms, _) ->
			postTerms(terms?.map { group -> group.termId } ?: return@observe)
		}
	}

	private fun postTerms(termIds: List<String>) {
		viewModelUser.getTermsByIds(termIds).observe(this) { (terms, _) ->
			this.currentTerm.value = terms?.min()?.id
			(root().listTerms.adapter as? AdapterTerm)?.update(terms ?: return@observe)
		}
	}

	private fun prepareProfileData() {
		viewModelUser.getUserById().observe(this) {
			root().userName.text = it?.name()
			root().userStudentNumber.text = it?.studentNumber
			it?.photoUrls?.values?.firstOrNull()?.also { image ->
				Picasso.get().load(image).into(root().userProfileImg)
			}
		}
	}
}
