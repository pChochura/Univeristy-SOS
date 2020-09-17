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

	fun get(): AppPreferences {
		if (instance == null) {
			throw ExceptionNotInitialized("You have to call init() first!")
		}

		return instance!!.prefs
	}

	companion object : Utils.SingletonHolder<Preferences, Context>(::Preferences) {

		const val KEY_ACCESS_TOKEN = "accessToken"
		const val KEY_SELECTED_UNIVERSITY = "selectedUniversity"

		const val KEY_TIMETABLE_START_HOUR = "timetableStartHour"
		const val KEY_TIMETABLE_END_HOUR = "timetableEndHour"
		const val KEY_TIMETABLE_VISIBLE_DAYS = "timetableVisibleDays"
		const val KEY_TIMETABLE_SNAP_TO_FULL_DAY = "timetableSnapToFullDay"
		const val KEY_TIMETABLE_ADD_EVENT = "timetableAddEvent"

		const val KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled"
		const val KEY_NOTIFICATIONS_GRADES = "notificationsGrades"
		const val KEY_NOTIFICATIONS_NEWS = "notificationsNews"
		const val KEY_NOTIFICATIONS_SURVEYS = "notificationsSurveys"

		const val KEY_SYSTEM_DARK_MODE = "systemDarkMode"
		const val KEY_SYSTEM_DEFAULT_TAB = "systemDefaultTab"
		const val KEY_SYSTEM_DEFAULT_LANGUAGE = "systemDefaultLanguage"
		const val KEY_SYSTEM_SEND_ANALYTICS = "systemSendAnalytics"

		const val KEY_PROFILE_SHORTCUTS = "profileShortcuts"
		const val KEY_PROFILE_SHORTCUTS_CLASS = "profileShortcutsClass"
		const val KEY_PROFILE_SHORTCUTS_DATA = "profileShortcutsData"

		fun get(): AppPreferences {
			if (instance == null) {
				throw ExceptionNotInitialized("You have to call init() first!")
			}

			return instance!!.prefs
		}
	}
}

fun AppPreferences.getAccessToken() = getJson<OAuth1AccessToken?>(Preferences.KEY_ACCESS_TOKEN)

fun AppPreferences.putAccessToken(accessToken: OAuth1AccessToken) =
	putJson(Preferences.KEY_ACCESS_TOKEN, accessToken)


fun AppPreferences.getSelectedUniversity() =
	getJson<University>(Preferences.KEY_SELECTED_UNIVERSITY)

fun AppPreferences.putSelectedUniversity(university: University) =
	putJson(Preferences.KEY_SELECTED_UNIVERSITY, university)


fun AppPreferences.getTimetableStartHour() =
	getInt(Preferences.KEY_TIMETABLE_START_HOUR, 6)

fun AppPreferences.putTimetableStartHour(value: Int) =
	put(Preferences.KEY_TIMETABLE_START_HOUR, value)


fun AppPreferences.getTimetableEndHour() =
	getInt(Preferences.KEY_TIMETABLE_END_HOUR, 20)

fun AppPreferences.putTimetableEndHour(value: Int) =
	put(Preferences.KEY_TIMETABLE_END_HOUR, value)


fun AppPreferences.getTimetableVisibleDays() =
	getInt(Preferences.KEY_TIMETABLE_VISIBLE_DAYS, 5)

fun AppPreferences.putTimetableVisibleDays(value: Int) =
	put(Preferences.KEY_TIMETABLE_VISIBLE_DAYS, value)


fun AppPreferences.getTimetableSnapToFullDay() =
	getBoolean(Preferences.KEY_TIMETABLE_SNAP_TO_FULL_DAY, true)

fun AppPreferences.putTimetableSnapToFullDay(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_SNAP_TO_FULL_DAY, value)


fun AppPreferences.getTimetableAddEvent() =
	getBoolean(Preferences.KEY_TIMETABLE_ADD_EVENT, false)

fun AppPreferences.putTimetableAddEvent(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_ADD_EVENT, value)


fun AppPreferences.getNotificationsEnabled() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_ENABLED, true)

fun AppPreferences.putNotificationsEnabled(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_ENABLED, value)


fun AppPreferences.getNotificationsGrades() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_GRADES, true)

fun AppPreferences.putNotificationsGrades(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_GRADES, value)


fun AppPreferences.getNotificationsNews() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_NEWS, true)

fun AppPreferences.putNotificationsNews(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_NEWS, value)


fun AppPreferences.getNotificationsSurveys() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_SURVEYS, true)

fun AppPreferences.putNotificationsSurveys(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_SURVEYS, value)


fun AppPreferences.getSystemDarkMode() =
	getBoolean(Preferences.KEY_SYSTEM_DARK_MODE, false)

fun AppPreferences.putSystemDarkMode(value: Boolean) =
	put(Preferences.KEY_SYSTEM_DARK_MODE, value)


fun AppPreferences.getSystemDefaultTab() =
	getInt(Preferences.KEY_SYSTEM_DEFAULT_TAB, 0)

fun AppPreferences.putSystemDefaultTab(value: Int) =
	put(Preferences.KEY_SYSTEM_DEFAULT_TAB, value)


fun AppPreferences.getSystemDefaultLanguage() =
	getString(Preferences.KEY_SYSTEM_DEFAULT_LANGUAGE, "PL")

fun AppPreferences.putSystemDefaultLanguage(value: String) =
	put(Preferences.KEY_SYSTEM_DEFAULT_LANGUAGE, value)


fun AppPreferences.getSendAnalytics() =
	getBoolean(Preferences.KEY_SYSTEM_SEND_ANALYTICS, true)

fun AppPreferences.putSendAnalytics(value: Boolean) =
	put(Preferences.KEY_SYSTEM_SEND_ANALYTICS, value)


fun AppPreferences.getProfileShortcuts() =
	getJson<List<Map<String, String>>>(Preferences.KEY_PROFILE_SHORTCUTS, "[]")

fun AppPreferences.putProfileShortcuts(shortcuts: List<Map<String, String>>) =
	putJson(Preferences.KEY_PROFILE_SHORTCUTS, shortcuts)
