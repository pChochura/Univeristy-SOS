package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R

class FragmentProfile : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_person
	override fun getNavigationName() = R.string.profile

	override fun created() {
	}
}