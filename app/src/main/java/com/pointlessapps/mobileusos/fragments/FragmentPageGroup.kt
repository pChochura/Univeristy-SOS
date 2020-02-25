package com.pointlessapps.mobileusos.fragments

import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterGroup
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import kotlinx.android.synthetic.main.fragment_page_group.*

class FragmentPageGroup(private val list: List<Group>?) : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_page_group

	override fun created() {
		prepareListGroup()
	}

	private fun prepareListGroup() {
		if (list == null) {
			return
		}

		root().post {
			listGroup.adapter = AdapterGroup(list.groupBy { it.courseName })
			listGroup.layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}
}