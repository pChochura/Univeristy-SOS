package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.toMB
import org.jetbrains.anko.find

class AdapterAttachment : AdapterSimple<Email.Attachment>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textTitle: AppCompatTextView
	private lateinit var textDescription: AppCompatTextView
	private lateinit var image: AppCompatImageView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_email_item

	override fun onCreate(root: View) {
		super.onCreate(root)
		textTitle = root.find(R.id.itemTitle)
		textDescription = root.find(R.id.itemDescription)
		image = root.find(R.id.itemImg)

		image.setImageResource(R.drawable.ic_attachment)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textTitle.text = list[position].filename
		textDescription.text = "%.2f MB".format(list[position].size?.toMB())
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
