package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.databinding.ListItemTermBinding
import com.pointlessapps.mobileusos.models.Term
import java.text.SimpleDateFormat
import java.util.*

class AdapterTerm :
	AdapterCore<Term, ListItemTermBinding>(mutableListOf(), ListItemTermBinding::class.java) {

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemTermBinding, position: Int) {
		binding.termName.text = list[position].name.toString()
		binding.termId.text = list[position].id
		binding.termFinishDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
			list[position].endDate ?: return
		)
	}
}
