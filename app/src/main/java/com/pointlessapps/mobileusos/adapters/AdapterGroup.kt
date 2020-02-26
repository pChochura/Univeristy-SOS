package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.keyAt
import com.pointlessapps.mobileusos.utils.valueAt
import org.jetbrains.anko.find

class AdapterGroup(private val groups: Map<Name?, List<Group>>) :
	AdapterSimple<Name?>(groups.keys.toMutableList()) {

	private lateinit var title: AppCompatTextView
	private lateinit var listClasstype: RecyclerView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_group

	override fun onCreate(root: View, position: Int) {
		title = root.find(R.id.textTitle)
		listClasstype = root.find(R.id.listClasstype)
	}

	override fun onBind(root: View, position: Int) {
		title.text = groups.keyAt(position).toString()

		prepareListClasstype(position)
	}

	private fun prepareListClasstype(position: Int) {
		listClasstype.apply {
			layoutManager = UnscrollableLinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
			adapter =
				AdapterClasstype(groups.valueAt(position).map { it.classType.toString() }.toMutableList())
		}
	}
}
