package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R

class FragmentMail : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_mail
	override fun getNavigationName() = R.string.mail

	override fun created() {
		
	}
}
