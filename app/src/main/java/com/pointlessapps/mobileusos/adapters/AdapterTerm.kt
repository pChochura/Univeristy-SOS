package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Term
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class AdapterTerm : AdapterSimple<Term>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textId: AppCompatTextView
	private lateinit var textFinishDate: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_term

	override fun onCreate(root: View, position: Int) {
		super.onCreate(root, position)
		textName = root.find(R.id.termName)
		textId = root.find(R.id.termId)
		textFinishDate = root.find(R.id.termFinishDate)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position].name.toString()
		textId.text = list[position].id
		textFinishDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
			list[position].endDate ?: return
		)
	}

	override fun update(list: List<Term>) {
		val sortedList = list.sorted()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}
}
