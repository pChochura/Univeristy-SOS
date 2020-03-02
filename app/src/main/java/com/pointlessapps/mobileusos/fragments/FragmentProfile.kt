package com.pointlessapps.mobileusos.fragments

import androidx.core.widget.NestedScrollView
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterPagerGroup
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.getTabs
import com.pointlessapps.mobileusos.viewModels.ViewModelProfile
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : FragmentBase() {

	private val viewModelProfile by viewModels<ViewModelProfile>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_person
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareViewPagerGroup()

		observeProfileData()
		observeGroupData()

		forceRefresh = true
	}

	private fun observeGroupData() {
		viewModelProfile.getAllGroups().observe(this) {
			if (it == null) {
				return@observe
			}

			postTerms(it.map { group -> group.termId })
			postGrades(it)
			(viewPagerGroup.adapter as? AdapterPagerGroup)?.update(it)
		}
	}

	private fun observeProfileData() {
		viewModelProfile.getUserById().observe(this) {
			if (it == null) {
				return@observe
			}

			textUserName.text = it.name()
			textStudentNumber.text = it.studentNumber
			it.photoUrls?.values?.firstOrNull()?.also { image ->
				Picasso.get().load(image).into(imageAvatar)
			}
		}
	}

	private fun postGrades(groups: List<Group>) {
		viewModelProfile.getGradesByGroups(groups).observe(this) {
			if (it == null) {
				return@observe
			}

			(viewPagerGroup.adapter as? AdapterPagerGroup)?.updateGrades(it)
		}
	}

	private fun postTerms(termIds: List<String>) {
		viewModelProfile.getTermsByIds(termIds).observe(this) {
			if (it == null) {
				return@observe
			}

			(viewPagerGroup.adapter as? AdapterPagerGroup)?.updateTerms(it)
			repeat(it.size - tabLayoutTerm.getTabs().count()) {
				tabLayoutTerm.addTab(tabLayoutTerm.newTab())
			}
		}
	}

	private fun prepareViewPagerGroup() {
		root().post {
			tabLayoutTerm.setupWithViewPager(viewPagerGroup)
			viewPagerGroup.adapter = AdapterPagerGroup(childFragmentManager)
			(viewPagerGroup.parent as? NestedScrollView)?.also {
				it.setPadding(it.paddingLeft, it.paddingTop, it.paddingRight, bottomNavigationView?.height ?: it.paddingBottom)
			}
		}
	}
}