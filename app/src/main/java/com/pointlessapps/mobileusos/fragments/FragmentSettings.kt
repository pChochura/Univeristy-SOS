package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.adapters.AdapterSimple
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.utils.DialogUtil
import kotlinx.android.synthetic.main.dialog_list_picker.*
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.dialog_time_picker.*
import kotlinx.android.synthetic.main.dialog_time_picker.buttonPrimary
import kotlinx.android.synthetic.main.dialog_time_picker.buttonSecondary
import kotlinx.android.synthetic.main.dialog_time_picker.title
import kotlinx.android.synthetic.main.fragment_settings.view.*
import org.jetbrains.anko.doAsync

class FragmentSettings : FragmentBase() {

	private val prefs = Preferences.get()

	override fun getLayoutId() = R.layout.fragment_settings

	override fun created() {
		prepareTimetableSettings()
		prepareNotificationsSettings()
		prepareSystemNotifications()
	}

	private fun prepareTimetableSettings() {
		root().itemVisiblePeriod.apply {
			value = {
				"%02d:00 - %02d:00".format(
					prefs.getTimetableStartHour(),
					prefs.getTimetableEndHour()
				)
			}
			onTapped { item ->
				DialogUtil.create(requireContext(), R.layout.dialog_time_picker, { dialog ->
					var currentMin = prefs.getTimetableStartHour().toFloat()
					var currentMax = prefs.getTimetableEndHour().toFloat()
					dialog.periodPicker.apply {
						setLabelFormatter { "%02.0f:00".format(it) }
						setValues(currentMin, currentMax)
						addOnChangeListener { slider, _, _ ->
							slider.values.minOrNull()?.also { currentMin = it }
							slider.values.maxOrNull()?.also { currentMax = it }
						}
					}
					dialog.buttonSecondary
						.setOnClickListener { dialog.dismiss() }
					dialog.buttonPrimary.setOnClickListener {
						prefs.putTimetableStartHour(currentMin.toInt())
						prefs.putTimetableEndHour(currentMax.toInt())

						item.refresh()
						dialog.dismiss()
					}
				}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
			}
		}

		root().itemVisibleDays.apply {
			value = { prefs.getTimetableVisibleDays().toString() }
			onTapped { item ->
				DialogUtil.create(requireContext(), R.layout.dialog_list_picker, { dialog ->
					dialog.title.text =
						getString(R.string.number_of_visible_days_title)
					dialog.listItems.apply {
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
										onForceRefreshAllFragments?.invoke()
										dialog.dismiss()

										item.refresh()
									}
								}
							}
						}
						layoutManager =
							LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
					}
				}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
			}
		}

		root().itemSnapToFullDay.apply {
			valueSwitch = { prefs.getTimetableSnapToFullDay() }
			onTapped { prefs.putTimetableSnapToFullDay(!prefs.getTimetableSnapToFullDay()) }
		}

		root().itemAddEvent.apply {
			valueSwitch = { prefs.getTimetableAddEvent() }
			onTapped { prefs.putTimetableAddEvent(!prefs.getTimetableAddEvent()) }
		}
	}

	private fun prepareNotificationsSettings() {
		root().itemEnableNotifications.apply {
			valueSwitch = { prefs.getNotificationsEnabled() }
			onTapped {
				prefs.putNotificationsEnabled(!prefs.getNotificationsEnabled())
				root().itemGradesNotifications.refresh()
				root().itemNewsNotifications.refresh()
				root().itemSurveysNotifications.refresh()
			}
		}

		root().itemGradesNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsGrades() }
			onTapped { prefs.putNotificationsGrades(!prefs.getNotificationsGrades()) }
		}

		root().itemNewsNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsNews() }
			onTapped { prefs.putNotificationsNews(!prefs.getNotificationsNews()) }
		}

		root().itemSurveysNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsSurveys() }
			onTapped { prefs.putNotificationsSurveys(!prefs.getNotificationsSurveys()) }
		}
	}

	private fun prepareSystemNotifications() {
		root().itemDarkMode.apply {
			valueSwitch = { prefs.getSystemDarkMode() }
			onTapped {
				prefs.putSystemDarkMode(!prefs.getSystemDarkMode())
				onForceRecreate?.invoke()
			}
		}

		root().itemDefaultTab.apply {
			value = {
				listOf(
					getString(R.string.timetable),
					getString(R.string.calendar),
					getString(R.string.mail),
					getString(R.string.news),
					getString(R.string.profile)
				)[prefs.getSystemDefaultTab()]
			}
			onTapped {
				DialogUtil.create(requireContext(), R.layout.dialog_list_picker, { dialog ->
					dialog.title.text =
						getString(R.string.default_tab_title)
					dialog.listItems.apply {
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
			}
		}

		root().itemDefaultLanguage.apply {
			value = { prefs.getSystemDefaultLanguage() ?: getString(R.string.pl) }
			onTapped {
				when (prefs.getSystemDefaultLanguage()) {
					getString(R.string.pl) -> getString(R.string.en)
					else -> getString(R.string.pl)
				}.also {
					prefs.putSystemDefaultLanguage(it)
					LocaleHelper.withLocale(context)
					onForceRecreate?.invoke()
				}
			}
		}

		root().itemLoogut.onTapped {
			DialogUtil.create(
				object : DialogUtil.StatefulDialog() {
					override fun toggle() {
						dialog.progressBar.isVisible = true
						dialog.messageMain.setText(R.string.loading)
						dialog.messageSecondary.isGone = true
						dialog.buttonPrimary.isGone = true
						dialog.buttonSecondary.isGone = true
					}
				},
				requireContext(), R.layout.dialog_loading, { dialog ->
					dialog.messageMain.setText(R.string.are_you_sure)
					dialog.messageSecondary.setText(R.string.logout_description)
					dialog.buttonPrimary.setText(R.string.logout)
					dialog.buttonPrimary.setOnClickListener {
						toggle()
						doAsync {
							Preferences.get().clear()
							AppDatabase.init(requireContext()).clearAllTables()
							dialog.dismiss()
							requireActivity().apply {
								startActivity(
									Intent(
										requireContext(),
										ActivityLogin::class.java
									)
								)
								finish()
							}
						}
					}
					dialog.buttonSecondary.setOnClickListener { dialog.dismiss() }
				}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT
			)
		}
	}
}
