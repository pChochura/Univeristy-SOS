package com.pointlessapps.mobileusos.adapters

import androidx.appcompat.widget.AppCompatTextView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemEmailAttachmentBinding
import com.pointlessapps.mobileusos.databinding.ListItemEmailItemAddBinding
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.toMB
import org.jetbrains.anko.find

class AdapterAttachment(private val canAddAttachment: Boolean = false) :
	AdapterCore<Email.Attachment, ViewBinding>(mutableListOf(), ViewBinding::class.java) {

	var onAddClickListener: (() -> Unit)? = null

	init {
		setHasStableIds(true)
	}

	override fun getBindingClass(viewType: Int) =
		if (viewType == +ViewType.SIMPLE) {
			ListItemEmailAttachmentBinding::class.java
		} else {
			ListItemEmailItemAddBinding::class.java
		}

	override fun onBindViewHolder(holder: ViewHolder<ViewBinding>, position: Int) =
		onBind(holder.binding, position)

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

	override fun onBind(binding: ViewBinding, position: Int) {
		binding.root.setOnClickListener {
			if (getItemViewType(position) == +ViewType.ADD) {
				onAddClickListener?.invoke()
			} else {
				onClickListener?.invoke(list[position])
			}
		}
		if (position < list.size) {
			binding.root.find<AppCompatTextView>(R.id.attachmentName).text =
				list[position].filename
			binding.root.find<AppCompatTextView>(R.id.attachmentSize).text =
				"%.2f MB".format(list[position].size?.toMB())
		}
	}
}
