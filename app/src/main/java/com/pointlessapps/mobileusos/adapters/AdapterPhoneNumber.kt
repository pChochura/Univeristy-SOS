package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Building
import org.jetbrains.anko.find

class AdapterPhoneNumber : AdapterSimple<Building.PhoneNumber>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var textComment: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_phone_number
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.phoneNumber)
		textComment = root.find(R.id.comment)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position].number

		list[position].comment?.also {
			textComment.text = it.toString()
			textComment.isVisible = true
		}
	}

	override fun update(list: List<Building.PhoneNumber>) {
		wholeList.apply {
			clear()
			addAll(list)
		}
		super.update(list)
	}
}
