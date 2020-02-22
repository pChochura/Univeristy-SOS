package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.University
import org.jetbrains.anko.find

class AdapterUniversity : AdapterSimple<University>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textLocation: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_university

	override fun onCreate(root: View, position: Int) {
		super.onCreate(root, position)
		textName = root.find(R.id.textName)
		textLocation = root.find(R.id.textLocation)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener.invoke(list[position])
		}

		textName.text = list[position].name
		textLocation.text = list[position].location
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