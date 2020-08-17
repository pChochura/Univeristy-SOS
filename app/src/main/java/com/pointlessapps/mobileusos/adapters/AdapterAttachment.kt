package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.toMB
import org.jetbrains.anko.find
import org.jetbrains.anko.findOptional

class AdapterAttachment(private val canAddAttachment: Boolean = false) :
	AdapterSimple<Email.Attachment>(mutableListOf()) {

	var onAddClickListener: (() -> Unit)? = null
	private val wholeList = mutableListOf(*list.toTypedArray())

	private var textTitle: AppCompatTextView? = null
	private var textSize: AppCompatTextView? = null

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) =
		if (viewType == +ViewType.SIMPLE) {
			R.layout.list_item_email_attachment
		} else {
			R.layout.list_item_email_item_add
		}

	override fun onCreate(root: View) {
		super.onCreate(root)
		textTitle = root.findOptional(R.id.attachmentName)
		textSize = root.findOptional(R.id.attachmentSize)
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

		textTitle?.text = list[position].filename
		textSize?.text = "%.2f MB".format(list[position].size?.toMB())
	}

	override fun update(list: List<Email.Attachment>) {
		val sortedList = list.sorted()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}
}
