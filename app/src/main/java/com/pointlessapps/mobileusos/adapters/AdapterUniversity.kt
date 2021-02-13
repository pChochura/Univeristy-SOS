package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.databinding.ListItemUniversityBinding
import com.pointlessapps.mobileusos.models.University

class AdapterUniversity : AdapterCore<University, ListItemUniversityBinding>(
	mutableListOf(),
	ListItemUniversityBinding::class.java
) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	init {
		setHasStableIds(true)
	}

	override fun onBind(binding: ListItemUniversityBinding, position: Int) {
		binding.universityName.text = list[position].name
		binding.universityLocation.text = list[position].location
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
