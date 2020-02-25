package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterPagerGroup
import com.pointlessapps.mobileusos.viewModels.ViewModelProfile
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*

class FragmentProfile : FragmentBase() {

	private val viewModelProfile by viewModels<ViewModelProfile>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_person
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareVIewPagerGroup()

		observeProfileData()
		observeGroupData()
	}

	private fun observeGroupData() {
		viewModelProfile.getAllGroups().observe(this) {
			if (it == null) {
				return@observe
			}

			postTerms(it.map { group -> group.termId!! })
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

	private fun postTerms(termIds: List<String>?) {
		viewModelProfile.getTermsByIds(termIds ?: return).observe(this) {
			if (it == null) {
				return@observe
			}

			val selectedTerm = tabLayoutTerm.selectedTabPosition
			tabLayoutTerm.removeAllTabs()
			it.forEach { term ->
				tabLayoutTerm.addTab(tabLayoutTerm.newTab().apply {
					text = term.id
				})
			}
			tabLayoutTerm.selectTab(tabLayoutTerm.getTabAt(selectedTerm) ?: return@observe)
		}
	}

	private fun prepareVIewPagerGroup() {
		root().post {
			tabLayoutTerm.setupWithViewPager(viewPagerGroup)
			viewPagerGroup.adapter = AdapterPagerGroup(childFragmentManager)
		}
	}
}