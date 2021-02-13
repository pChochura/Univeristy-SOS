package com.pointlessapps.mobileusos.adapters

import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.databinding.ListItemPhoneNumberBinding
import com.pointlessapps.mobileusos.models.Building

class AdapterPhoneNumber : AdapterCore<Building.PhoneNumber, ListItemPhoneNumberBinding>(
	mutableListOf(),
	ListItemPhoneNumberBinding::class.java
) {

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemPhoneNumberBinding, position: Int) {
		binding.phoneNumber.text = list[position].number

		list[position].comment?.also {
			binding.comment.text = it.toString()
			binding.comment.isVisible = true
		}
	}
}
