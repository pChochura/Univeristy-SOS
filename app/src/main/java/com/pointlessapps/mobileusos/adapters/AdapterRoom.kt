package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.BuildingRoom
import org.jetbrains.anko.find

class AdapterRoom : AdapterSimple<BuildingRoom>(mutableListOf()) {

	private lateinit var textName: AppCompatTextView
	private lateinit var textCapacity: Chip

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_room
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.roomName)
		textCapacity = root.find(R.id.roomCapacity)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position].number
		textCapacity.text = list[position].capacity.toString()
	}
}
