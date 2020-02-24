package com.pointlessapps.mobileusos.helpers

import android.content.Context
import com.github.scribejava.core.model.OAuth1AccessToken
import com.pointlessapps.mobileusos.exceptions.ExceptionNotInitialized
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.getJson
import com.pointlessapps.mobileusos.utils.putJson
import net.grandcentrix.tray.AppPreferences

class Preferences private constructor(context: Context?) {

	private val prefs = AppPreferences(context)

	companion object : Utils.SingletonHolder<Preferences, Context>(::Preferences) {

		const val KEY_ACCESS_TOKEN = "accessToken"
		const val KEY_SELECTED_UNIVERSITY = "selectedUniversity"

		fun get(): AppPreferences {
			if (instance == null) {
				throw ExceptionNotInitialized("You have to call init() first!")
			}

			return instance!!.prefs
		}
	}
}

fun AppPreferences.getAccessToken() = getJson<OAuth1AccessToken>(Preferences.KEY_ACCESS_TOKEN)

fun AppPreferences.putAccessToken(accessToken: OAuth1AccessToken) =
	putJson(Preferences.KEY_ACCESS_TOKEN, accessToken)

fun AppPreferences.getSelectedUniversity() = getJson<University>(Preferences.KEY_SELECTED_UNIVERSITY)

fun AppPreferences.putSelectedUniversity(university: University) =
	putJson(Preferences.KEY_SELECTED_UNIVERSITY, university)