package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterTerm
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.partial_profile_shortcuts.view.*
import kotlinx.android.synthetic.main.partial_profile_terms.view.*

class FragmentProfile : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_profile
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareTermsList()
		prepareTerms()
		prepareProfileData()
		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		root().buttonGrades.setOnClickListener { onChangeFragmentListener?.invoke(FragmentGrades()) }
		root().buttonCourses.setOnClickListener { onChangeFragmentListener?.invoke(FragmentCourses()) }
		root().buttonSurveys.setOnClickListener { onChangeFragmentListener?.invoke(FragmentSurveys()) }
	}

	private fun prepareTermsList() {
		root().listTerms.setAdapter(AdapterTerm())
	}

	private fun prepareTerms() {
		viewModelUser.getAllGroups().observe(this) {
			postTerms(it?.map { group -> group.termId } ?: return@observe)
		}
	}

	private fun postTerms(termIds: List<String>) {
		viewModelUser.getTermsByIds(termIds).observe(this) { terms ->
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
