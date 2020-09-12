package com.pointlessapps.mobileusos.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.Utils.themeColor
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
		val hasSeparator = a.getBoolean(R.styleable.SettingsItem_hasSeparator, true)
		val isEnabled = a.getBoolean(R.styleable.SettingsItem_enabled, true)
		a.recycle()

		root.itemTitle.text = title
		root.itemDescription.text = description
		root.itemAction.isVisible = hasBubble
		root.itemSeparator.isVisible = hasSeparator

		setAction()
		setSwitchAction()

		enabled = { isEnabled }
		setEnabled(isEnabled)
	}

	private fun setEnabled(value: Boolean? = null) {
		val v = value ?: true && enabled()
		root.bg.isClickable = v
		root.bg.isFocusable = v
		root.bg.alpha = if (v) 1f else 0.3f
		root.bg.rippleColor =
			ColorStateList.valueOf(
				if (v) context.themeColor(R.attr.colorTextSecondary) else ContextCompat.getColor(
					context,
					android.R.color.transparent
				)
			)
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

	fun onTapped(callback: (SettingsItem) -> Unit) {
		root.bg.setOnClickListener {
			if (enabled()) {
				callback(this)
			}

			refresh()
		}
	}
}
