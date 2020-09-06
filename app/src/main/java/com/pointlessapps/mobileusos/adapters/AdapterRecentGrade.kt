package com.pointlessapps.mobileusos.adapters

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.updateLayoutParams
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.dp
import org.jetbrains.anko.find

class AdapterRecentGrade : AdapterSimple<Grade>(mutableListOf()) {

	private lateinit var textName: AppCompatTextView
	private lateinit var textValue: Chip

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_grade_square

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.gradeName)
		textValue = root.find(R.id.gradeValue)
	}

	override fun onBind(root: View, position: Int) {
		root.find<ViewGroup>(R.id.bg).updateLayoutParams<ViewGroup.MarginLayoutParams> {
			marginEnd = if (position != itemCount - 1) 20.dp else 0
		}

		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position].courseName?.toString()
		textValue.text = list[position].valueSymbol
	}
}
