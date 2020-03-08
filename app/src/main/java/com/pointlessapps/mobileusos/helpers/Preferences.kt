package com.pointlessapps.mobileusos.helpers

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.github.scribejava.core.model.OAuth1AccessToken
import com.pointlessapps.mobileusos.exceptions.ExceptionNotInitialized
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.getJson
import com.pointlessapps.mobileusos.utils.putJson
import net.grandcentrix.tray.AppPreferences
import java.util.*

class Preferences private constructor(context: Context?) {

	private val prefs = AppPreferences(context)

	companion object : Utils.SingletonHolder<Preferences, Context>(::Preferences) {

		const val KEY_ACCESS_TOKEN = "accessToken"
		const val KEY_SELECTED_UNIVERSITY = "selectedUniversity"
		const val KEY_BREAK_LENGTH = "breakLength"
		const val KEY_EVENT_COLOR = "eventColor"

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

fun AppPreferences.getSelectedUniversity() =
	getJson<University>(Preferences.KEY_SELECTED_UNIVERSITY)

fun AppPreferences.putSelectedUniversity(university: University) =
	putJson(Preferences.KEY_SELECTED_UNIVERSITY, university)

fun AppPreferences.getBreakLength() = getInt(Preferences.KEY_BREAK_LENGTH, 15)

fun AppPreferences.putBreakLength(breakLength: Int) =
	put(Preferences.KEY_BREAK_LENGTH, breakLength)

fun AppPreferences.getEventColorByClassType(classType: String): Int {
//	TODO: make this as a settings property
	return when (classType.toLowerCase(Locale.getDefault())) {
		"wyk" -> Color.parseColor("#FF80CBC4")
		"ćw" -> Color.parseColor("#FFF06292")
		"lab" -> Color.parseColor("#FFFF9E80")
		"wf" -> Color.parseColor("#FF81C784")
		else -> Color.parseColor("#FFAED581")
	}
//	return getInt("${Preferences.KEY_EVENT_COLOR}_${classType.toLowerCase(Locale.getDefault())}", 0)
}

fun AppPreferences.putEventColorByClassType(classType: String, color: Int) =
	put("${Preferences.KEY_EVENT_COLOR}_${classType.toLowerCase(Locale.getDefault())}", color)