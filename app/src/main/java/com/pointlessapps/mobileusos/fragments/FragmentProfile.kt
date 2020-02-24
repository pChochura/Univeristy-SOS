package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.viewModels.ViewModelProfile
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : FragmentBase() {

	private val viewModelProfile by viewModels<ViewModelProfile>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_person
	override fun getNavigationName() = R.string.profile

	override fun created() {
		viewModelProfile.getUserById(null).observe(this, Observer {
			textUserName.text = it?.name()
			textStudentNumber.text = it?.studentNumber
			it?.photoUrls?.values?.firstOrNull()?.also { image ->
				Picasso.get().load(image).into(imageAvatar)
			}
		})
	}
}