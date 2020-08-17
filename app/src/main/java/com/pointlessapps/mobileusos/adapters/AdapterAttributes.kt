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

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textValue: Chip

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_room_attribute
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.attributeName)
		textValue = root.find(R.id.attributeValue)
	}

	override fun onBind(root: View, position: Int) {
		root.find<ViewGroup>(R.id.bg).updateLayoutParams<ViewGroup.MarginLayoutParams> {
			topMargin = if (position != 0) 10.dp else 0
		}

		textName.text = list[position].description.toString()
		textValue.text =
			list[position].count?.toString() ?: root.context.getString(R.string.available)
	}

	override fun update(list: List<BuildingRoom.Attribute>) {
		wholeList.apply {
			clear()
			addAll(list)
		}
		super.update(list)
	}
}
