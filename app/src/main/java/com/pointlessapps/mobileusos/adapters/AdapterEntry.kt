package com.pointlessapps.mobileusos.adapters

import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemPageEntryBinding
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.utils.InternalLinkMovementMethod
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.isEmailProtocol
import com.squareup.picasso.Picasso

class AdapterEntry : AdapterCore<Chapter.Page.Entry, ListItemPageEntryBinding>(
	mutableListOf(),
	ListItemPageEntryBinding::class.java
) {

	init {
		setHasStableIds(true)
	}

	var onImageClickListener: ((String) -> Unit)? = null
	var onEmailClickListener: ((String) -> Unit)? = null

	override fun onBind(binding: ListItemPageEntryBinding, position: Int) {
		binding.entryImage.isVisible =
			list[position].imageUrls?.get("720x405")?.also {
				Picasso.get().load(it)
					.into(binding.entryImage.apply {
						setOnClickListener {
							onImageClickListener?.invoke(
								list[position].imageUrls?.get(
									"1440x810"
								) ?: return@setOnClickListener
							)
						}
					})
			} != null
		binding.entryName.text = list[position].title.toString()
		binding.entryContent.apply {
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
