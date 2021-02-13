package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.databinding.ListItemUserEmploymentFunctionBinding
import com.pointlessapps.mobileusos.models.User

class AdapterEmploymentFunction :
	AdapterCore<User.EmploymentFunction, ListItemUserEmploymentFunctionBinding>(
		mutableListOf(),
		ListItemUserEmploymentFunctionBinding::class.java
	) {

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemUserEmploymentFunctionBinding, position: Int) {
		binding.textFunction.text = list[position].function?.toString()
		binding.textFaculty.text = list[position].faculty.name?.toString()
	}
}
