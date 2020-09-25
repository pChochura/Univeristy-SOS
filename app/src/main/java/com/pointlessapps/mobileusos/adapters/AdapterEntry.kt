package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.utils.InternalLinkMovementMethod
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.isEmailProtocol
import com.squareup.picasso.Picasso
import org.jetbrains.anko.find

class AdapterEntry : AdapterSimple<Chapter.Page.Entry>(mutableListOf()) {

	init {
		setHasStableIds(true)
	}

	var onImageClickListener: ((String) -> Unit)? = null
	var onEmailClickListener: ((String) -> Unit)? = null

	override fun getLayoutId(viewType: Int) = R.layout.list_item_page_entry

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.entryImage).isVisible =
			list[position].imageUrls?.get("720x405")?.also {
				Picasso.get().load(it).into(root.find<AppCompatImageView>(R.id.entryImage).apply {
					setOnClickListener {
						onImageClickListener?.invoke(
							list[position].imageUrls?.get(
								"1440x810"
							) ?: return@setOnClickListener
						)
					}
				})
			} != null
		root.find<AppCompatTextView>(R.id.entryName).text = list[position].title.toString()
		root.find<AppCompatTextView>(R.id.entryContent).apply {
			text = Utils.parseHtml(list[position].content.toString())
			movementMethod = InternalLinkMovementMethod {
				if (it.isEmailProtocol()) {
					onEmailClickListener?.invoke(it.substringAfter("mailto:"))

					return@InternalLinkMovementMethod true
				}

				return@InternalLinkMovementMethod false
			}
		}
	}

	override fun update(list: List<Chapter.Page.Entry>) = super.update(list.sorted())
}
