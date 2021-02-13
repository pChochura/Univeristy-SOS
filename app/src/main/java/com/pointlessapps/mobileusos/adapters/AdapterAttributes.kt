package com.pointlessapps.mobileusos.adapters

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemRoomAttributeBinding
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.dp

class AdapterAttributes : AdapterCore<BuildingRoom.Attribute, ListItemRoomAttributeBinding>(
	mutableListOf(),
	ListItemRoomAttributeBinding::class.java
) {

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onBind(binding: ListItemRoomAttributeBinding, position: Int) {
		binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
			topMargin = if (position != 0) 10.dp else 0
		}

		binding.attributeName.text =
			list[position].description.toString()
		binding.attributeValue.text =
			list[position].count?.toString() ?: binding.root.context.getString(R.string.available)
	}
}
