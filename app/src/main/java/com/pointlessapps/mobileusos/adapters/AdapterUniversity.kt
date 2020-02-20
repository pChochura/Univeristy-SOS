package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.University
import org.jetbrains.anko.find

class AdapterUniversity(private val currentList: MutableList<University>) : AdapterSimple<University>(currentList) {

	private val wholeList = listOf(*currentList.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textLocation: AppCompatTextView

	init {
		setHasStableIds(true)
		showMatching("")
	}

	override fun getLayoutId() = R.layout.list_item_university

	override fun onCreate(root: View, position: Int) {
		super.onCreate(root, position)
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener.invoke(position)
		}

		textName = root.find(R.id.textName)
		textLocation = root.find(R.id.textLocation)
	}

	override fun onBind(root: View, position: Int) {
		textName.text = currentList[position].name
		textLocation.text = currentList[position].location
	}

	fun showMatching(text: String) {
		currentList.clear()
		currentList.addAll(wholeList.filter { it.matches(text) })
		notifyDataSetChanged()
	}
}