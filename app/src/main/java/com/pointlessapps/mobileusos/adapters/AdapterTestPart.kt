package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Test
import org.jetbrains.anko.find

class AdapterTestPart(list: List<Test.Node>) : AdapterSimple<Test.Node>(list.toMutableList()) {

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_test_part

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		root.find<AppCompatTextView>(R.id.testPartName).text = list[position].name.toString()
		root.find<Chip>(R.id.testPartValue).text =
			list[position].resultObject?.grade?.symbol
				?: list[position].resultObject?.points.toString()
	}
}
