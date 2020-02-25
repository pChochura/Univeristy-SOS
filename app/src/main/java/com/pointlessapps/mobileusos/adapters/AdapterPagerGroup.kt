package com.pointlessapps.mobileusos.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.pointlessapps.mobileusos.fragments.FragmentPageGroup
import com.pointlessapps.mobileusos.models.Group

class AdapterPagerGroup(fragmentManager: FragmentManager) :
	FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val list = mutableMapOf<String?, List<Group>>()

	fun update(list: List<Group>) {
		this.list.apply {
			clear()
			putAll(list.groupBy { it.termId })
		}
		notifyDataSetChanged()
	}

	override fun getItem(position: Int) =
		FragmentPageGroup(list.values.elementAt(position))

	override fun getCount() = list.keys.size
}