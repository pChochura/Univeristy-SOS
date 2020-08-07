package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.capitalize
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class AdapterEmail : AdapterSimple<Email>(mutableListOf()) {

	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textSubject: AppCompatTextView
	private lateinit var textStatus: AppCompatTextView
	private lateinit var textDate: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_email

	override fun onCreate(root: View) {
		super.onCreate(root)
		textSubject = root.find(R.id.emailSubject)
		textDate = root.find(R.id.emailDate)
		textStatus = root.find(R.id.emailStatus)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textSubject.text = list[position].subject
		textDate.text = dateFormat.format(list[position].date ?: return)
		textStatus.text = list[position].status?.capitalize()
	}

	override fun update(list: List<Email>) {
		val sortedList = list.sortedDescending()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}
}
