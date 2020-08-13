package com.pointlessapps.mobileusos.views

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import org.jetbrains.anko.find

class SettingsItem(
	context: Context,
	private val title: String = "",
	private val description: String = "",
	private val action: (() -> String)? = null,
	private val switch: (() -> Boolean)? = null,
	private val enabled: (() -> Boolean)? = null,
	ID: Int = View.generateViewId(),
	private val onClickListener: (SettingsItem.() -> Unit)? = null
) : ConstraintLayout(context) {
	constructor(context: Context) : this(context, "")

	private val bg: MaterialCardView
	private val textTitle: AppCompatTextView
	private val textDescription: AppCompatTextView
	private val textAction: Chip

	init {
		id = ID

		val root: View = View.inflate(
			context,
			R.layout.settings_item,
			this
		)

		bg = root.find(R.id.bg)
		textTitle = root.find(R.id.itemTitle)
		textDescription = root.find(R.id.itemDescription)
		textAction = root.find(R.id.itemAction)

		refresh()
	}

	fun refresh() {
		textTitle.text = title
		textDescription.text = description

		setAction(textAction)

		if (enabled?.invoke() != false) {
			bg.alpha = 1f
			bg.isClickable = true
			bg.isFocusable = true
			bg.setRippleColorResource(R.color.colorTextSecondary)

			onClickListener?.also { callback ->
				bg.setOnClickListener {
					callback()
					setAction(textAction)

					refresh()
				}
			}
		} else {
			bg.alpha = 0.3f
			bg.isClickable = false
			bg.isFocusable = false
			bg.setRippleColorResource(android.R.color.transparent)
		}
	}

	private fun setAction(textAction: Chip) {
		action?.also {
			textAction.apply {
				text = it()
				isVisible = true
			}
		}
		switch?.also {
			textAction.apply {
				text = context.getString(
					if (it() && enabled?.invoke() != false) {
						R.string.on
					} else {
						R.string.off
					}
				)
				setChipBackgroundColorResource(
					if (it() && enabled?.invoke() != false) {
						R.color.colorAccent
					} else {
						R.color.colorTextSecondary
					}
				)
				isVisible = true
			}
		}
	}
}
