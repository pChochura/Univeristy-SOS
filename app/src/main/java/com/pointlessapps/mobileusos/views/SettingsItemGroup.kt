package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.res.use
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.SettingsItemGroupBinding

class SettingsItemGroup(
	context: Context,
	attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

	private val binding = SettingsItemGroupBinding.inflate(LayoutInflater.from(context), this, true)

	init {
		context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsItemGroup, 0, 0).use {
			binding.textHeader.text = it.getString(R.styleable.SettingsItemGroup_header)
		}

		if (binding.textHeader.text.isNullOrBlank()) {
			binding.textHeader.isVisible = false
		}
	}

	fun refreshItems() {
		binding.itemsContainer.forEach {
			if (it is SettingsItem) {
				it.refresh()
			}
		}
	}

	override fun onViewAdded(child: View) {
		if (child !is SettingsItem) {
			return
		}

		binding.itemsContainer.also {
			it.addView(child.apply {
				(parent as ViewGroup).removeView(this)
			})
		}
	}
	override fun getOrientation() = VERTICAL
}
