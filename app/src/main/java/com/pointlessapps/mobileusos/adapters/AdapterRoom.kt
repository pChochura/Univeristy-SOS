package com.pointlessapps.mobileusos.adapters

import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.databinding.ListItemRoomBinding
import com.pointlessapps.mobileusos.models.BuildingRoom

class AdapterRoom : AdapterCore<BuildingRoom, ListItemRoomBinding>(
	mutableListOf(),
	ListItemRoomBinding::class.java
) {
	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemRoomBinding, position: Int) {
		binding.roomName.text = list[position].number
		list[position].capacity?.toString()?.also {
			binding.roomCapacity.isVisible = true
			binding.roomCapacity.text = it
		}
	}
}
