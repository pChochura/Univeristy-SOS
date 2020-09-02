package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import com.pointlessapps.mobileusos.R
import kotlinx.android.synthetic.main.settings_item.view.*

class SettingsItem(
	context: Context,
	attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {
	constructor(context: Context) : this(context, null)

	private val root: View = View.inflate(
		context,
		R.layout.settings_item,
		this
	)

	var enabled: (() -> Boolean) = { true }
		set(value) {
			field = value
			setEnabled()
		}

	var value: (() -> String) = { "" }
		set(value) {
			field = value
			setAction()
		}

	var valueSwitch: (() -> Boolean)? = null
		set(value) {
			field = value
			setSwitchAction()
		}

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsItem, 0, 0)
		val title = a.getString(R.styleable.SettingsItem_title)
		val description = a.getString(R.styleable.SettingsItem_description)
		val hasBubble = a.getBoolean(R.styleable.SettingsItem_hasBubble, true)
		a.recycle()

		root.itemTitle.text = title
		root.itemDescription.text = description
		root.itemAction.isGone = !hasBubble

		isClickable = true
		isFocusable = true

		setAction()
		setSwitchAction()
		setEnabled()
	}

	private fun setEnabled() {
		enabled().also {
			root.bg.isClickable = it
			root.bg.isFocusable = it
			root.bg.alpha = if (it) 1f else 0.3f
		}
	}

	private fun setAction() {
		root.itemAction.text = value()
	}

	private fun setSwitchAction() {
		if (valueSwitch?.invoke() ?: return) {
			root.itemAction.setText(R.string.on)
			root.itemAction.setChipBackgroundColorResource(R.color.colorAccent)
		} else {
			root.itemAction.setText(R.string.off)
			root.itemAction.setChipBackgroundColorResource(R.color.colorTextSecondary)
		}
	}

	fun refresh() {
		setEnabled()
		setAction()
		setSwitchAction()
	}

	fun onTapped(callback: (SettingsItem) -> Unit) = root.bg.setOnClickListener {
		callback(this)
		refresh()
	}
}
