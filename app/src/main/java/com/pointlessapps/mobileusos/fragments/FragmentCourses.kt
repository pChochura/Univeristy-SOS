package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterCourse
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_courses.view.*

class FragmentCourses : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_courses

	override fun created() {
		prepareCoursesList()
		prepareCourses()
	}

	private fun prepareCourses() {
		viewModelUser.getAllGroups().observe(this) { (groups, online) ->
			val termIds = groups?.map { group -> group.termId } ?: return@observe

			viewModelUser.getTermsByIds(termIds).observe(this) { (terms, online2) ->
				val values = groups.groupBy { it.termId }
					.mapValues { it.value.groupBy { group -> group.courseId } }

				(root().listCourses.adapter as? AdapterCourse)?.notifyDataChanged(
					terms?.map {
						AdapterCourse.SectionHeader(
							it,
							values[it.id]?.values?.toList()!!
						)
					}
				)

				root().listCourses.apply {
					setEmptyText(getString(R.string.no_courses))
					setLoaded(online && online2)
				}
			}
		}
	}

	private fun prepareCoursesList() {
		root().listCourses.setAdapter(AdapterCourse(requireContext()).apply {
			onClickListener = {
				onChangeFragmentListener?.invoke(FragmentCourse(it))
			}
		})
	}

}
