package com.pointlessapps.mobileusos.adapters

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pointlessapps.mobileusos.fragments.FragmentPageGroup
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.keyAt
import com.pointlessapps.mobileusos.utils.valueAt

class AdapterPagerGroup(fragmentManager: FragmentManager) :
	FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val list = mutableMapOf<String?, List<Group>>()

	fun update(list: List<Group>) {
		this.list.apply {
			clear()
			putAll(list.groupBy { it.termId }.toSortedMap(Utils.STRING_COMPARATOR))
		}
		notifyDataSetChanged()
	}

	override fun getItem(position: Int) =
		FragmentPageGroup(list.valueAt(position))

	override fun getCount() = list.keys.size

	override fun getPageTitle(position: Int) = list.keyAt(position)
}