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

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_email

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		root.find<AppCompatTextView>(R.id.emailSubject).text =
			list[position].subject?.takeIf(String::isNotBlank)
				?: root.context.getString(R.string.no_subject)
		root.find<AppCompatTextView>(R.id.emailDate).text =
			dateFormat.format(list[position].date ?: return)
		root.find<AppCompatTextView>(R.id.emailStatus).text = list[position].status?.capitalize()
	}

	override fun update(list: List<Email>) = super.update(list.sortedDescending())
}
