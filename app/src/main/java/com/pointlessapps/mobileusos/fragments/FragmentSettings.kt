package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R

class FragmentSettings : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_settings
	override fun getNavigationIcon() = R.drawable.ic_settings
	override fun getNavigationName() = R.string.settings

	override fun created() {
	}
}