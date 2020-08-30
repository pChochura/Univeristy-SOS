package com.pointlessapps.mobileusos.fragments

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.slider.RangeSlider
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterSimple
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.views.SettingsItem
import com.pointlessapps.mobileusos.views.SettingsItemGroup
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.find

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
					getString(R.string.visible_period),
					getString(R.string.visible_period_description),
					{
						"%02d:00 - %02d:00".format(
							prefs.getTimetableStartHour(),
							prefs.getTimetableEndHour()
						)
					}
				) {
					DialogUtil.create(requireContext(), R.layout.dialog_time_picker, { dialog ->
						var currentMin = prefs.getTimetableStartHour().toFloat()
						var currentMax = prefs.getTimetableEndHour().toFloat()
						dialog.find<RangeSlider>(R.id.periodPicker).apply {
							setLabelFormatter { "%02.0f:00".format(it) }
							setValues(currentMin, currentMax)

							addOnChangeListener { _, _, _ ->
							}
						}

						dialog.find<View>(R.id.buttonSecondary)
							.setOnClickListener { dialog.dismiss() }
						dialog.find<View>(R.id.buttonPrimary).setOnClickListener {
							prefs.putTimetableStartHour(currentMin.toInt())
							prefs.putTimetableEndHour(currentMax.toInt())
						}
					}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
				},
				SettingsItem(
					requireContext(),
					getString(R.string.number_of_visible_days),
					getString(R.string.number_of_visible_days_description),
					{ prefs.getTimetableVisibleDays().toString() }
				) {
					DialogUtil.create(requireContext(), R.layout.dialog_list_picker, { dialog ->
						dialog.find<AppCompatTextView>(R.id.title).text =
							getString(R.string.number_of_visible_days_title)
						dialog.find<RecyclerView>(R.id.listItems).apply {
							adapter = object :
								AdapterSimple<String>((3..7).map(Int::toString).toMutableList()) {
								override fun getLayoutId(viewType: Int) = R.layout.list_item_simple
								override fun onBind(root: View, position: Int) {
									(root as? MaterialButton)?.apply {
										text = list[position]
										setOnClickListener {
											prefs.putTimetableVisibleDays(
												list[position].toIntOrNull()
													?: prefs.getTimetableVisibleDays()
											)
											dialog.dismiss()
											refresh()
										}
									}
								}
							}
							layoutManager =
								LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
						}
					}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
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
					DialogUtil.create(requireContext(), R.layout.dialog_list_picker, { dialog ->
						dialog.find<AppCompatTextView>(R.id.title).text =
							getString(R.string.default_tab_title)
						dialog.find<RecyclerView>(R.id.listItems).apply {
							adapter = object :
								AdapterSimple<String>(
									mutableListOf(
										getString(R.string.timetable),
										getString(R.string.calendar),
										getString(R.string.mail),
										getString(R.string.news),
										getString(R.string.profile)
									)
								) {
								override fun getLayoutId(viewType: Int) = R.layout.list_item_simple
								override fun onBind(root: View, position: Int) {
									(root as? MaterialButton)?.apply {
										text = list[position]
										setOnClickListener {
											prefs.putSystemDefaultTab(position)
											dialog.dismiss()
											refresh()
										}
									}
								}
							}
							layoutManager =
								LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
						}
					}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
				},
				SettingsItem(
					requireContext(),
					getString(R.string.default_language),
					getString(R.string.default_language_description),
					{ prefs.getSystemDefaultLanguage() ?: getString(R.string.pl) }
				) {
					when (prefs.getSystemDefaultLanguage()) {
						getString(R.string.pl) -> getString(R.string.en)
						else -> getString(R.string.pl)
					}.also {
						prefs.putSystemDefaultLanguage(it)
						LocaleHelper.withLocale(context)
						onForceRecreate?.invoke()
					}
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
