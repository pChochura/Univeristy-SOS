package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.utils.getJson
import com.pointlessapps.mobileusos.utils.putJson

interface FragmentPinnable {

	/**
	 * first - @DrawableRes of the icon
	 * second - text string
	 */
	fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) = Unit

	fun isPinned(className: String, data: String) = Preferences.get().run {
		getJson<MutableList<Map<String, String>>>("profileShortcuts").find {
			it["profileShortcuts_class"] == className && it["profileShortcuts_data"] == data
		} != null
	}

	fun togglePin(className: String, data: String): Boolean {
		val prefs = Preferences.get()
		val shortcuts = prefs.getJson<MutableList<Map<String, String>>>("profileShortcuts")
		val index = shortcuts.indexOfFirst {
			it["profileShortcuts_class"] == className && it["profileShortcuts_data"] == data
		}
		if (index == -1) {
			shortcuts.add(
				mapOf(
					"profileShortcuts_class" to className,
					"profileShortcuts_data" to data,
				)
			)
			prefs.putJson("profileShortcuts", shortcuts)

			return true
		}

		shortcuts.removeAt(index)
		prefs.putJson("profileShortcuts", shortcuts)

		return false
	}
}
