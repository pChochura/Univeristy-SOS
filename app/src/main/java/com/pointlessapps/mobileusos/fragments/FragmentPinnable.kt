package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getProfileShortcuts
import com.pointlessapps.mobileusos.helpers.putProfileShortcuts

interface FragmentPinnable {

	/**
	 * first - @DrawableRes of the icon
	 * second - text string
	 */
	fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) = Unit

	fun isPinned(className: String, data: String) = Preferences.get().run {
		getProfileShortcuts().find {
			it[Preferences.KEY_PROFILE_SHORTCUTS_CLASS] == className && it[Preferences.KEY_PROFILE_SHORTCUTS_DATA] == data
		} != null
	}

	fun togglePin(className: String, data: String): Boolean {
		val prefs = Preferences.get()
		val shortcuts = prefs.getProfileShortcuts().toMutableList()
		val index = shortcuts.indexOfFirst {
			it[Preferences.KEY_PROFILE_SHORTCUTS_CLASS] == className && it[Preferences.KEY_PROFILE_SHORTCUTS_DATA] == data
		}
		if (index == -1) {
			shortcuts.add(
				mapOf(
					Preferences.KEY_PROFILE_SHORTCUTS_CLASS to className,
					Preferences.KEY_PROFILE_SHORTCUTS_DATA to data,
				)
			)
			prefs.putProfileShortcuts(shortcuts)

			return true
		}

		shortcuts.removeAt(index)
		prefs.putProfileShortcuts(shortcuts)

		return false
	}
}
