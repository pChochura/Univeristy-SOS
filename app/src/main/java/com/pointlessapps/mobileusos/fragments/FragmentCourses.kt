package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterCourse
import com.pointlessapps.mobileusos.databinding.FragmentCoursesBinding
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.viewModels.ViewModelUser

class FragmentCourses :
	FragmentCoreImpl<FragmentCoursesBinding>(FragmentCoursesBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun created() {
		prepareCoursesList()
		prepareCourses()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		prepareCourses {
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareCourses(callback: (() -> Unit)? = null) {
		var finished = false
		viewModelUser.getAllGroups().observe(this) { (groups) ->
			val termIds = groups.sorted().map { group -> group.termId }

			viewModelUser.getTermsByIds(termIds).observe(this) { (terms) ->
				val values = groups.groupBy { it.termId }
					.mapValues {
						it.value.groupBy { group -> group.courseId }
							.mapValues { values -> values.value.sortedBy(Course::classTypeId) }
					}

				(binding().listCourses.adapter as? AdapterCourse)?.notifyDataChanged(
					terms.map { term ->
						AdapterCourse.SectionHeader(
							term,
							values[term.id]?.values?.toList()
								?.sortedBy { it.firstOrNull()?.courseId }!!
						)
					}
				)

				binding().listCourses.apply {
					setEmptyText(getString(R.string.no_courses))
					setLoaded(false)
				}
			}.onFinished {
				if (finished) {
					binding().listCourses.setLoaded(true)
					callback?.invoke()
				}
			}
		}.onFinished { finished = true }
	}

	private fun prepareCoursesList() {
		binding().listCourses.setAdapter(AdapterCourse(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentCourse(it))
			}
		})
	}

}
