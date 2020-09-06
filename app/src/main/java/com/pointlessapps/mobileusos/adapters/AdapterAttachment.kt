package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.toMB
import org.jetbrains.anko.find

class AdapterAttachment(private val canAddAttachment: Boolean = false) :
	AdapterSimple<Email.Attachment>(mutableListOf()) {

	var onAddClickListener: (() -> Unit)? = null

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) =
		if (viewType == +ViewType.SIMPLE) {
			R.layout.list_item_email_attachment
		} else {
			R.layout.list_item_email_item_add
		}

	override fun onBindViewHolder(holder: DataObjectHolder, position: Int) =
		onBind(holder.root, position)

	override fun getItemCount() = if (canAddAttachment) {
		list.size + 1
	} else {
		list.size
	}

	override fun getItemViewType(position: Int) =
		if (position == list.size) {
			+ViewType.ADD
		} else {
			+ViewType.SIMPLE
		}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			if (getItemViewType(position) == +ViewType.ADD) {
				onAddClickListener?.invoke()
			} else {
				onClickListener?.invoke(list[position])
			}
		}
		if (position < list.size) {
			root.find<AppCompatTextView>(R.id.attachmentName).text =
				list[position].filename
			root.find<AppCompatTextView>(R.id.attachmentSize).text =
				"%.2f MB".format(list[position].size?.toMB())
		}
	}
}
