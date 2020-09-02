package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import com.pointlessapps.mobileusos.R
import kotlinx.android.synthetic.main.settings_item_group.view.*

class SettingsItemGroup(
	context: Context,
	attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {

	private val root: View? = View.inflate(
		context,
		R.layout.settings_item_group,
		this
	)

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.SettingsItemGroup, 0, 0)
		val header = a.getString(R.styleable.SettingsItemGroup_header)
		a.recycle()

		root?.textHeader?.text = header
	}

	override fun onViewAdded(child: View?) {
		root?.itemsContainer?.addView(child?.apply {
			(parent as ViewGroup).removeView(this)
		})
	}
}
