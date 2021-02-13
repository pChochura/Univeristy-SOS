package com.pointlessapps.mobileusos.adapters

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemGradeSquareBinding
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.dp

class AdapterRecentGrade : AdapterCore<Grade, ListItemGradeSquareBinding>(
	mutableListOf(),
	ListItemGradeSquareBinding::class.java
) {
	init {
		setHasStableIds(true)
	}

	override fun onBind(binding: ListItemGradeSquareBinding, position: Int) {
		binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
			marginEnd = if (position != itemCount - 1) 20.dp else 0
		}

		binding.gradeName.text =
			list[position].courseName?.toString()
				?: binding.root.context.getString(R.string.loading)
		binding.gradeValue.text = list[position].valueSymbol
	}
}
