package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R

class FragmentTimetable : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_timetable
	override fun getNavigationIcon() = R.drawable.ic_timetable
	override fun getNavigationName() = R.string.timetable

	override fun created() {
	}
}