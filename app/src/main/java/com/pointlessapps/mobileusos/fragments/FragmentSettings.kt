package com.pointlessapps.mobileusos.fragments

import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.views.SettingsItem
import com.pointlessapps.mobileusos.views.SettingsItemGroup
import kotlinx.android.synthetic.main.fragment_settings.view.*

class FragmentSettings : FragmentBase() {

	private val prefs = Preferences.get()

	companion object {
		const val ID_NOTIFICATIONS_GRADES = 1
		const val ID_NOTIFICATIONS_NEWS = 2
		const val ID_NOTIFICATIONS_SURVEYS = 3
	}

	override fun getLayoutId() = R.layout.fragment_settings

	override fun created() {
		prepareSettings()
	}

	private fun prepareSettings() {
		root().settingsContainer.addView(getGroupTimetable())
		root().settingsContainer.addView(getGroupNotifications())
		root().settingsContainer.addView(getGroupSystem())
	}

	private fun getGroupTimetable() =
		SettingsItemGroup(
			requireContext(),
			getString(R.string.timetable)
		) {
			listOf(
				SettingsItem(
					requireContext(),
					getString(R.string.start_hour),
					getString(R.string.start_hour_description),
					{ "%02d:00".format(prefs.getTimetableStartHour()) }
				) {

				},
				SettingsItem(
					requireContext(),
					getString(R.string.end_hour),
					getString(R.string.end_hour_description),
					{ "%02d:00".format(prefs.getTimetableEndHour()) }
				) {

				},
				SettingsItem(
					requireContext(),
					getString(R.string.number_of_visible_days),
					getString(R.string.number_of_visible_days_description),
					{ prefs.getTimetableVisibleDays().toString() }
				) {

				},
				SettingsItem(
					requireContext(),
					getString(R.string.snap_to_a_full_day),
					getString(R.string.snap_to_a_full_day_description),
					switch = { prefs.getTimetableSnapToFullDay() }
				) {
					prefs.putTimetableSnapToFullDay(!prefs.getTimetableSnapToFullDay())
				},
				SettingsItem(
					requireContext(),
					getString(R.string.add_an_event),
					getString(R.string.add_an_event_description),
					switch = { prefs.getTimetableAddEvent() }
				) {
					prefs.putTimetableAddEvent(!prefs.getTimetableAddEvent())
				}
			)
		}

	private fun getGroupNotifications() =
		SettingsItemGroup(
			requireContext(),
			getString(R.string.notifications)
		) {
			listOf(
				SettingsItem(
					requireContext(),
					getString(R.string.enable_notifications),
					getString(R.string.enable_notifications_description),
					switch = { prefs.getNotificationsEnabled() }
				) {
					prefs.putNotificationsEnabled(!prefs.getNotificationsEnabled())
					refreshByIds(
						ID_NOTIFICATIONS_GRADES,
						ID_NOTIFICATIONS_NEWS,
						ID_NOTIFICATIONS_SURVEYS
					)
				},
				SettingsItem(
					requireContext(),
					getString(R.string.grades_notifications),
					getString(R.string.grades_notifications_description),
					switch = { prefs.getNotificationsGrades() },
					enabled = { prefs.getNotificationsEnabled() },
					ID = ID_NOTIFICATIONS_GRADES
				) {
					prefs.putNotificationsGrades(!prefs.getNotificationsGrades())
				},
				SettingsItem(
					requireContext(),
					getString(R.string.news_notifications),
					getString(R.string.news_notifications_description),
					switch = { prefs.getNotificationsNews() },
					enabled = { prefs.getNotificationsEnabled() },
					ID = ID_NOTIFICATIONS_NEWS
				) {
					prefs.putNotificationsNews(!prefs.getNotificationsNews())
				},
				SettingsItem(
					requireContext(),
					getString(R.string.surveys_notifications),
					getString(R.string.surveys_notifications_description),
					switch = { prefs.getNotificationsSurveys() },
					enabled = { prefs.getNotificationsEnabled() },
					ID = ID_NOTIFICATIONS_SURVEYS
				) {
					prefs.putNotificationsSurveys(!prefs.getNotificationsSurveys())
				}
			)
		}

	private fun getGroupSystem() =
		SettingsItemGroup(
			requireContext(),
			getString(R.string.system)
		) {
			listOf(
				SettingsItem(
					requireContext(),
					getString(R.string.dark_mode),
					getString(R.string.dark_mode_description),
					switch = {
						prefs.getSystemDarkMode()
					}
				) {
					prefs.putSystemDarkMode(!prefs.getSystemDarkMode())
					onForceRecreate?.invoke()
				},
				SettingsItem(
					requireContext(),
					getString(R.string.default_tab),
					getString(R.string.default_tab_description),
					{
						listOf(
							getString(R.string.timetable),
							getString(R.string.calendar),
							getString(R.string.mail),
							getString(R.string.news),
							getString(R.string.profile)
						)[prefs.getSystemDefaultTab()]
					}
				) {

				},
				SettingsItem(
					requireContext(),
					getString(R.string.default_language),
					getString(R.string.default_language_description),
					{ prefs.getSystemDefaultLanguage() ?: getString(R.string.pl) }
				) {
					prefs.putSystemDefaultLanguage(
						when (prefs.getSystemDefaultLanguage()) {
							getString(R.string.pl) -> getString(R.string.en)
							else -> getString(R.string.pl)
						}
					)
				},
				SettingsItem(
					requireContext(),
					getString(R.string.logout),
					getString(R.string.logout_description)
				) {

				}
			)
		}

}
