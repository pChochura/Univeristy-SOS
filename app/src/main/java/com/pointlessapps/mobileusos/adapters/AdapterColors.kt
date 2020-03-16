package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.views.WeekView
import org.jetbrains.anko.find

class AdapterColors(arr: IntArray) : AdapterSimple<Int>(arr.toMutableList()) {
	override fun getLayoutId() = R.layout.list_item_color

	override fun onBind(root: View, position: Int) {
		val item = root.findViewById<AppCompatImageView>(R.id.color_circle)
		item.setColorFilter(list[position])
		item.setOnClickListener {
			onClickList.invoke(list[position])
		}

	}

	fun setOnClickList(event : (Int) -> Unit) {
		onClickList = event
	}

	private lateinit var onClickList : (Int) -> Unit

}