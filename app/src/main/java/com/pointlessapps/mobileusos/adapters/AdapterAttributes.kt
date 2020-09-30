package com.pointlessapps.mobileusos.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updateLayoutParams
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.utils.dp
import org.jetbrains.anko.find

class AdapterAttributes : AdapterSimple<BuildingRoom.Attribute>(mutableListOf()) {

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_room_attribute
	override fun isCollapsible() = true

	override fun onBind(root: View, position: Int) {
		root.find<ViewGroup>(R.id.bg).updateLayoutParams<ViewGroup.MarginLayoutParams> {
			topMargin = if (position != 0) 10.dp else 0
		}

		root.find<AppCompatTextView>(R.id.attributeName).text =
			list[position].description.toString()
		root.find<Chip>(R.id.attributeValue).text =
			list[position].count?.toString() ?: root.context.getString(R.string.available)
	}
}
