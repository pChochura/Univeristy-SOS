package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.University
import org.jetbrains.anko.find

class AdapterUniversity : AdapterSimple<University>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_university

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		root.find<AppCompatTextView>(R.id.universityName).text = list[position].name
		root.find<AppCompatTextView>(R.id.universityLocation).text = list[position].location
	}

	override fun update(list: List<University>) {
		val sortedList = list.sorted()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}

	fun showMatching(text: String) {
		list.apply {
			clear()
			addAll(wholeList.filter { it.matches(text) }.sorted())
		}
		notifyDataSetChanged()
	}
}
