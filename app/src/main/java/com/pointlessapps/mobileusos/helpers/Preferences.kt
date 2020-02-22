package com.pointlessapps.mobileusos.helpers

import android.content.Context
import com.pointlessapps.mobileusos.exceptions.ExceptionNotInitialized
import com.pointlessapps.mobileusos.utils.Utils
import net.grandcentrix.tray.AppPreferences

class Preferences private constructor(context: Context) {

	private val prefs = AppPreferences(context)

	companion object : Utils.SingletonHolder<Preferences, Context>(::Preferences) {

		const val KEY_ACCESS_TOKEN = "accessToken"

		fun get(): AppPreferences {
			if (instance == null) {
				throw ExceptionNotInitialized("You have to call init() first!")
			}

			return instance!!.prefs
		}
	}
}