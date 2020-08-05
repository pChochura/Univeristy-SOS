package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R

class FragmentCalendar : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_calendar
	override fun getNavigationName() = R.string.calendar

	override fun created() {
		
	}
}
