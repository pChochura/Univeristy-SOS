package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import org.jetbrains.anko.find

class AdapterPhoneNumber() : AdapterSimple<String>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_phone_number
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.phoneNumber)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position]
	}

	override fun update(list: List<String>) {
		wholeList.apply {
			clear()
			addAll(list)
		}
		super.update(list)
	}
}
