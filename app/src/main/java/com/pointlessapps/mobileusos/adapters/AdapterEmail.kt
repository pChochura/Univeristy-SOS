package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemEmailBinding
import com.pointlessapps.mobileusos.models.Email
import java.text.SimpleDateFormat
import java.util.*

class AdapterEmail :
	AdapterCore<Email, ListItemEmailBinding>(mutableListOf(), ListItemEmailBinding::class.java) {

	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

	init {
		setHasStableIds(true)
	}

	override fun onBind(binding: ListItemEmailBinding, position: Int) {
		binding.emailSubject.text = list[position].subject?.takeIf(String::isNotBlank)
			?: binding.root.context.getString(R.string.no_subject)
		binding.emailDate.text = dateFormat.format(list[position].date ?: return)
		binding.emailStatus.text = list[position].status?.capitalize(Locale.getDefault())
	}

	override fun update(list: List<Email>) = super.update(list.sortedDescending())
}
