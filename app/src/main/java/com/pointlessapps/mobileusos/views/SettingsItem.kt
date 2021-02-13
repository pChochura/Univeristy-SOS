package com.pointlessapps.mobileusos.views

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.SettingsItemBinding
import com.pointlessapps.mobileusos.utils.Utils.themeColor

class SettingsItem(
	context: Context,
	attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {
	constructor(context: Context) : this(context, null)

	private val binding: SettingsItemBinding =
		SettingsItemBinding.inflate(LayoutInflater.from(context), this, true)

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

		binding.itemTitle.text = title
		binding.itemDescription.text = description
		binding.itemAction.isVisible = hasBubble
		binding.itemSeparator.isVisible = hasSeparator

		setAction()
		setSwitchAction()

		enabled = { isEnabled }
		setEnabled(isEnabled)
	}

	private fun setEnabled(value: Boolean? = null) {
		val v = value ?: true && enabled()
		binding.bg.isClickable = v
		binding.bg.isFocusable = v
		binding.bg.alpha = if (v) 1f else 0.3f
		binding.bg.rippleColor =
			ColorStateList.valueOf(
				if (v) context.themeColor(R.attr.colorTextSecondary) else ContextCompat.getColor(
					context,
					android.R.color.transparent
				)
			)
	}

	private fun setAction() {
		binding.itemAction.text = value()
	}

	private fun setSwitchAction() {
		if (valueSwitch?.invoke() ?: return) {
			binding.itemAction.setText(R.string.on)
			binding.itemAction.setChipBackgroundColorResource(R.color.colorAccent)
		} else {
			binding.itemAction.setText(R.string.off)
			binding.itemAction.setChipBackgroundColorResource(R.color.colorTextSecondary)
		}
	}

	fun refresh() {
		setEnabled()
		setAction()
		setSwitchAction()
	}

	fun onTapped(callback: (SettingsItem) -> Unit) {
		binding.bg.setOnClickListener {
			if (enabled()) {
				callback(this)
			}

			refresh()
		}
	}
}
