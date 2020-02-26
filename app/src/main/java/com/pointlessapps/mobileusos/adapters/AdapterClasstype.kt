package com.pointlessapps.mobileusos.adapters

import android.view.View
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import org.jetbrains.anko.find

class AdapterClasstype(list: MutableList<String>) : AdapterSimple<String>(list) {

	override fun getLayoutId() = R.layout.list_item_classtype

	override fun onBind(root: View, position: Int) {
		root.find<MaterialButton>(R.id.textTitle).text = list[position]
	}
}