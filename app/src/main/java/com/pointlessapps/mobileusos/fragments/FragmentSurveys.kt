package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.viewModels.ViewModelUser

class FragmentSurveys : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_surveys

	override fun created() {

	}
}
