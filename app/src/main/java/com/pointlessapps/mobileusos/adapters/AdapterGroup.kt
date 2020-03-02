package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.utils.keyAt
import com.pointlessapps.mobileusos.utils.valueAt
import org.jetbrains.anko.find

class AdapterGroup(private val groups: Map<Course, List<Group>>) :
	AdapterSimple<Course>(groups.keys.toMutableList()) {

	private lateinit var textTitle: AppCompatTextView
	private lateinit var textGrade: AppCompatTextView
	private lateinit var listClasstype: RecyclerView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_group

	override fun onCreate(root: View, position: Int) {
		textTitle = root.find(R.id.textTitle)
		textGrade = root.find(R.id.textGrade)
		listClasstype = root.find(R.id.listClasstype)
	}

	override fun onBind(root: View, position: Int) {
		textTitle.text = groups.keyAt(position).courseName.toString()
		groups.valueAt(position).first().grade?.also {
			textGrade.text = it.toString()
		}

		prepareListClasstype(position)
	}

	private fun prepareListClasstype(position: Int) {
		listClasstype.apply {
			layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
			adapter =
				AdapterClasstype(groups.valueAt(position).map { it.classType.toString() }.toMutableList())
		}
	}
}
