package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Name
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
		title.text = groups.keys.elementAt(position).toString()

		listClasstype.apply {
			layoutManager =
				object : LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) {
					override fun canScrollVertically() = false
				}
			adapter = object : AdapterSimple<String>(
				groups.values.elementAt(position)
					.map { it.classType.toString() }.toMutableList()
			) {
				override fun getLayoutId() = R.layout.list_item_classtype
				override fun onBind(root: View, position: Int) {
					root.find<MaterialButton>(R.id.textTitle).text = list[position]
				}
			}
		}
	}
}
