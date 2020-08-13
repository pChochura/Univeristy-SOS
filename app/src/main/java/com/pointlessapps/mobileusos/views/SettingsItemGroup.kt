package com.pointlessapps.mobileusos.views

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import com.pointlessapps.mobileusos.utils.dp
import org.jetbrains.anko.find

class SettingsItemGroup(
	context: Context,
	header: String = "",
	getItemsCallback: SettingsItemGroup.() -> List<SettingsItem> = { listOf() }
) : LinearLayoutCompat(context) {
	constructor(context: Context) : this(context, "")

	init {
		val root = View.inflate(
			context,
			R.layout.settings_item_group,
			this
		)

		root.find<AppCompatTextView>(R.id.textHeader).text = header
		val items = getItemsCallback()
		items.forEachIndexed { index, item ->
			root.find<ViewGroup>(R.id.itemsContainer).apply {
				addView(item)
				if (index < items.size - 1) {
					addView(
						View(context).apply { setBackgroundColor(context.themeColor(R.attr.colorBackground)) },
						LayoutParams(LayoutParams.MATCH_PARENT, 1.dp)
					)
				}
			}
		}
	}

	fun refreshByIds(vararg ids: Int) {
		ids.forEach { find<SettingsItem>(it).refresh() }
	}
}
