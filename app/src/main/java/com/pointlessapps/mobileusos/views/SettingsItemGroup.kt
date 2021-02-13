package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.SettingsItemGroupBinding

class SettingsItemGroup(
	context: Context,
	attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

	private val binding = SettingsItemGroupBinding.inflate(LayoutInflater.from(context), this, true)

	init {
		context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsItemGroup, 0, 0).apply {
			binding.textHeader.text = getString(R.styleable.SettingsItemGroup_header)
		}.recycle()
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
