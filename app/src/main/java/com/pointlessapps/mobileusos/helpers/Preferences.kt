package com.pointlessapps.mobileusos.helpers

import android.content.Context
import android.content.res.Configuration
import com.github.scribejava.core.model.OAuth1AccessToken
import com.pointlessapps.mobileusos.exceptions.ExceptionNotInitialized
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.models.WidgetConfiguration
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.getJson
import com.pointlessapps.mobileusos.utils.putJson
import net.grandcentrix.tray.AppPreferences
import org.jetbrains.anko.configuration

class Preferences private constructor(context: Context?) {

	private val prefs = AppPreferencesWrapper(context)

	fun get(): AppPreferencesWrapper {
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
		const val KEY_TIMETABLE_OUTLINE_REMOTE = "timetableOutlineRemote"
		const val KEY_TIMETABLE_MISSING_BREAKS = "timetableMissingBreaks"

		const val KEY_NOTIFICATIONS_ENABLED = "notificationsEnabled"
		const val KEY_NOTIFICATIONS_GRADES = "notificationsGrades"
		const val KEY_NOTIFICATIONS_NEWS = "notificationsNews"
		const val KEY_NOTIFICATIONS_SURVEYS = "notificationsSurveys"

		const val KEY_SYSTEM_DARK_MODE = "systemDarkMode"
		const val KEY_SYSTEM_DEFAULT_TAB = "systemDefaultTab"
		const val KEY_SYSTEM_DEFAULT_LANGUAGE = "systemDefaultLanguage"
		const val KEY_SYSTEM_SEND_ANALYTICS = "systemSendAnalytics"

		const val KEY_SYSTEM_LAUNCH_COUNT = "systemLaunchCount"
		const val KEY_SYSTEM_REVIEW_DISCARDED = "systemReviewDiscarded"

		const val KEY_PROFILE_SHORTCUTS = "profileShortcuts"
		const val KEY_PROFILE_SHORTCUTS_CLASS = "profileShortcutsClass"
		const val KEY_PROFILE_SHORTCUTS_DATA = "profileShortcutsData"

		const val KEY_WIDGET_CONFIGURATION = "widgetConfiguration"

		const val KEY_SCOPE_OTHER_EMAILS = "scopeOtherEmails"
		const val KEY_SCOPE_CRS_TESTS = "scopeCrsTests"
		const val KEY_SCOPE_MAIL_CLIENT = "scopeMailClient"
		const val KEY_SCOPE_SURVEY_FILLING = "scopeSurveyFilling"
		const val KEY_SCOPE_EVENTS = "scopeEvents"

		fun get(): AppPreferencesWrapper {
			if (instance == null) {
				throw ExceptionNotInitialized("You have to call init() first!")
			}

			return instance!!.prefs
		}
	}
}

class AppPreferencesWrapper(context: Context?) : AppPreferences(context) {
	public override fun getContext(): Context = super.getContext()
}

fun AppPreferencesWrapper.getAccessToken() =
	getJson<OAuth1AccessToken?>(Preferences.KEY_ACCESS_TOKEN)

fun AppPreferencesWrapper.putAccessToken(accessToken: OAuth1AccessToken) =
	putJson(Preferences.KEY_ACCESS_TOKEN, accessToken)


fun AppPreferencesWrapper.getSelectedUniversity() =
	getJson<University>(Preferences.KEY_SELECTED_UNIVERSITY)

fun AppPreferencesWrapper.putSelectedUniversity(university: University) =
	putJson(Preferences.KEY_SELECTED_UNIVERSITY, university)


fun AppPreferencesWrapper.getTimetableStartHour() =
	getInt(Preferences.KEY_TIMETABLE_START_HOUR, 6)

fun AppPreferencesWrapper.putTimetableStartHour(value: Int) =
	put(Preferences.KEY_TIMETABLE_START_HOUR, value)


fun AppPreferencesWrapper.getTimetableEndHour() =
	getInt(Preferences.KEY_TIMETABLE_END_HOUR, 20)

fun AppPreferencesWrapper.putTimetableEndHour(value: Int) =
	put(Preferences.KEY_TIMETABLE_END_HOUR, value)


fun AppPreferencesWrapper.getTimetableVisibleDays() =
	getInt(Preferences.KEY_TIMETABLE_VISIBLE_DAYS, 5)

fun AppPreferencesWrapper.putTimetableVisibleDays(value: Int) =
	put(Preferences.KEY_TIMETABLE_VISIBLE_DAYS, value)


fun AppPreferencesWrapper.getTimetableSnapToFullDay() =
	getBoolean(Preferences.KEY_TIMETABLE_SNAP_TO_FULL_DAY, true)

fun AppPreferencesWrapper.putTimetableSnapToFullDay(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_SNAP_TO_FULL_DAY, value)


fun AppPreferencesWrapper.getTimetableAddEvent() =
	getBoolean(Preferences.KEY_TIMETABLE_ADD_EVENT, false)

fun AppPreferencesWrapper.putTimetableAddEvent(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_ADD_EVENT, value)


fun AppPreferencesWrapper.getTimetableOutlineRemote() =
	getBoolean(Preferences.KEY_TIMETABLE_OUTLINE_REMOTE, false)

fun AppPreferencesWrapper.putTimetableOutlineRemote(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_OUTLINE_REMOTE, value)


fun AppPreferencesWrapper.getTimetableMissingBreaks() =
	getBoolean(Preferences.KEY_TIMETABLE_MISSING_BREAKS, true)

fun AppPreferencesWrapper.putTimetableMissingBreaks(value: Boolean) =
	put(Preferences.KEY_TIMETABLE_MISSING_BREAKS, value)


fun AppPreferencesWrapper.getNotificationsEnabled() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_ENABLED, true)

fun AppPreferencesWrapper.putNotificationsEnabled(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_ENABLED, value)


fun AppPreferencesWrapper.getNotificationsGrades() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_GRADES, true)

fun AppPreferencesWrapper.putNotificationsGrades(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_GRADES, value)


fun AppPreferencesWrapper.getNotificationsNews() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_NEWS, true)

fun AppPreferencesWrapper.putNotificationsNews(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_NEWS, value)


fun AppPreferencesWrapper.getNotificationsSurveys() =
	getBoolean(Preferences.KEY_NOTIFICATIONS_SURVEYS, true)

fun AppPreferencesWrapper.putNotificationsSurveys(value: Boolean) =
	put(Preferences.KEY_NOTIFICATIONS_SURVEYS, value)


fun AppPreferencesWrapper.getSystemDarkMode() =
	getBoolean(
		Preferences.KEY_SYSTEM_DARK_MODE,
		context.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
	)

fun AppPreferencesWrapper.putSystemDarkMode(value: Boolean) =
	put(Preferences.KEY_SYSTEM_DARK_MODE, value)


fun AppPreferencesWrapper.getSystemDefaultTab() =
	getInt(Preferences.KEY_SYSTEM_DEFAULT_TAB, 0)

fun AppPreferencesWrapper.putSystemDefaultTab(value: Int) =
	put(Preferences.KEY_SYSTEM_DEFAULT_TAB, value)


fun AppPreferencesWrapper.getSystemDefaultLanguage() =
	getString(Preferences.KEY_SYSTEM_DEFAULT_LANGUAGE, "PL")

fun AppPreferencesWrapper.putSystemDefaultLanguage(value: String) =
	put(Preferences.KEY_SYSTEM_DEFAULT_LANGUAGE, value)


fun AppPreferencesWrapper.getSystemSendAnalytics() =
	getBoolean(Preferences.KEY_SYSTEM_SEND_ANALYTICS, true)

fun AppPreferencesWrapper.putSystemSendAnalytics(value: Boolean) =
	put(Preferences.KEY_SYSTEM_SEND_ANALYTICS, value)


fun AppPreferencesWrapper.getSystemLaunchCount() =
	getInt(Preferences.KEY_SYSTEM_LAUNCH_COUNT, 0).also {
		put(Preferences.KEY_SYSTEM_LAUNCH_COUNT, it + 1)
	}


fun AppPreferencesWrapper.getSystemReviewDiscarded() =
	getBoolean(Preferences.KEY_SYSTEM_REVIEW_DISCARDED, false)

fun AppPreferencesWrapper.putSystemReviewDiscarded(value: Boolean) =
	put(Preferences.KEY_SYSTEM_REVIEW_DISCARDED, value)


fun AppPreferencesWrapper.getProfileShortcuts() =
	getJson<List<Map<String, String>>>(Preferences.KEY_PROFILE_SHORTCUTS, "[]")

fun AppPreferencesWrapper.putProfileShortcuts(shortcuts: List<Map<String, String>>) =
	putJson(Preferences.KEY_PROFILE_SHORTCUTS, shortcuts)


fun AppPreferencesWrapper.getWidgetConfiguration() =
	getJson(Preferences.KEY_WIDGET_CONFIGURATION, WidgetConfiguration())

fun AppPreferencesWrapper.putWidgetConfiguration(widgetConfig: WidgetConfiguration) =
	putJson(Preferences.KEY_WIDGET_CONFIGURATION, widgetConfig)


fun AppPreferencesWrapper.getScopeOtherEmails() =
	getBoolean(Preferences.KEY_SCOPE_OTHER_EMAILS, true)

fun AppPreferencesWrapper.putScopeOtherEmails(value: Boolean) =
	put(Preferences.KEY_SCOPE_OTHER_EMAILS, value)


fun AppPreferencesWrapper.getScopeCrsTests() =
	getBoolean(Preferences.KEY_SCOPE_CRS_TESTS, true)

fun AppPreferencesWrapper.putScopeCrsTests(value: Boolean) =
	put(Preferences.KEY_SCOPE_CRS_TESTS, value)


fun AppPreferencesWrapper.getScopeMailClient() =
	getBoolean(Preferences.KEY_SCOPE_MAIL_CLIENT, true)

fun AppPreferencesWrapper.putScopeMailClient(value: Boolean) =
	put(Preferences.KEY_SCOPE_MAIL_CLIENT, value)


fun AppPreferencesWrapper.getScopeSurveyFilling() =
	getBoolean(Preferences.KEY_SCOPE_SURVEY_FILLING, true)

fun AppPreferencesWrapper.putScopeSurveyFilling(value: Boolean) =
	put(Preferences.KEY_SCOPE_SURVEY_FILLING, value)


fun AppPreferencesWrapper.getScopeEvents() =
	getBoolean(Preferences.KEY_SCOPE_EVENTS, true)

fun AppPreferencesWrapper.putScopeEvents(value: Boolean) =
	put(Preferences.KEY_SCOPE_EVENTS, value)