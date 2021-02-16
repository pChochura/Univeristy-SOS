package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.adapters.AdapterCore
import com.pointlessapps.mobileusos.databinding.*
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.utils.DialogUtil
import org.jetbrains.anko.doAsync

class FragmentSettings :
	FragmentCoreImpl<FragmentSettingsBinding>(FragmentSettingsBinding::class.java) {

	private val prefs = Preferences.get()

	override fun created() {
		prepareTimetableSettings()
		prepareNotificationsSettings()
		prepareSystemNotifications()
	}

	private fun prepareTimetableSettings() {
		binding().itemVisiblePeriod.apply {
			value = {
				"%02d:00 - %02d:00".format(
					prefs.getTimetableStartHour(),
					prefs.getTimetableEndHour()
				)
			}
			onTapped { item ->
				DialogUtil.create(requireContext(), DialogTimePickerBinding::class.java, { dialog ->
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
					dialog.buttonSecondary.setOnClickListener { dismiss() }
					dialog.buttonPrimary.setOnClickListener {
						prefs.putTimetableStartHour(currentMin.toInt())
						prefs.putTimetableEndHour(currentMax.toInt())

						onForceRefreshAllFragments?.invoke()

						item.refresh()
						dismiss()
					}
				})
			}
		}

		binding().itemVisibleDays.apply {
			value = { prefs.getTimetableVisibleDays().toString() }
			onTapped { item ->
				DialogUtil.create(requireContext(), DialogListPickerBinding::class.java, { dialog ->
					dialog.title.setText(R.string.number_of_visible_days_title)
					dialog.listItems.apply {
						adapter = object :
							AdapterCore<String, ListItemSimpleBinding>(
								(3..7).map(Int::toString).toMutableList(),
								ListItemSimpleBinding::class.java
							) {
							override fun onBind(binding: ListItemSimpleBinding, position: Int) {
								binding.root.apply {
									text = list[position]
									setOnClickListener {
										prefs.putTimetableVisibleDays(
											list[position].toIntOrNull()
												?: prefs.getTimetableVisibleDays()
										)
										onForceRefreshAllFragments?.invoke()

										item.refresh()
										dismiss()
									}
								}
							}
						}
						layoutManager =
							LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
					}
				})
			}
		}

		binding().itemSnapToFullDay.apply {
			valueSwitch = { prefs.getTimetableSnapToFullDay() }
			onTapped { prefs.putTimetableSnapToFullDay(!prefs.getTimetableSnapToFullDay()) }
		}

		binding().itemOutlineRemote.apply {
			valueSwitch = { prefs.getTimetableOutlineRemote() }
			onTapped {
				prefs.putTimetableOutlineRemote(!prefs.getTimetableOutlineRemote())
				onForceRecreate?.invoke()
			}
		}

		binding().itemAddMissingBreaks.apply {
			valueSwitch = { prefs.getTimetableMissingBreaks() }
			onTapped {
				prefs.putTimetableMissingBreaks(!prefs.getTimetableMissingBreaks())
				onForceRecreate?.invoke()
			}
		}

		binding().itemAddEvent.apply {
			valueSwitch = { prefs.getTimetableAddEvent() }
			onTapped { prefs.putTimetableAddEvent(!prefs.getTimetableAddEvent()) }
		}
	}

	private fun prepareNotificationsSettings() {
		binding().itemEnableNotifications.apply {
			valueSwitch = { prefs.getNotificationsEnabled() }
			onTapped {
				prefs.putNotificationsEnabled(!prefs.getNotificationsEnabled())
				binding().itemGradesNotifications.refresh()
				binding().itemNewsNotifications.refresh()
				binding().itemSurveysNotifications.refresh()
			}
		}

		binding().itemGradesNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsGrades() }
			onTapped { prefs.putNotificationsGrades(!prefs.getNotificationsGrades()) }
		}

		binding().itemNewsNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsNews() }
			onTapped { prefs.putNotificationsNews(!prefs.getNotificationsNews()) }
		}

		binding().itemSurveysNotifications.apply {
			enabled = { prefs.getNotificationsEnabled() }
			valueSwitch = { prefs.getNotificationsSurveys() }
			onTapped { prefs.putNotificationsSurveys(!prefs.getNotificationsSurveys()) }
		}
	}

	private fun prepareSystemNotifications() {
		binding().itemDarkMode.apply {
			valueSwitch = { prefs.getSystemDarkMode() }
			onTapped {
				prefs.putSystemDarkMode(!prefs.getSystemDarkMode())
				onForceRecreate?.invoke()
			}
		}

		binding().itemDefaultTab.apply {
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
				DialogUtil.create(requireContext(), DialogListPickerBinding::class.java, { dialog ->
					dialog.title.setText(R.string.default_tab_title)
					dialog.listItems.apply {
						adapter = object :
							AdapterCore<String, ListItemSimpleBinding>(
								mutableListOf(
									getString(R.string.timetable),
									getString(R.string.calendar),
									getString(R.string.mail),
									getString(R.string.news),
									getString(R.string.profile)
								),
								ListItemSimpleBinding::class.java
							) {
							override fun onBind(binding: ListItemSimpleBinding, position: Int) {
								binding.root.apply {
									text = list[position]
									setOnClickListener {
										prefs.putSystemDefaultTab(position)
										dismiss()
										refresh()
									}
								}
							}
						}
						layoutManager =
							LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
					}
				})
			}
		}

		binding().itemDefaultLanguage.apply {
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

		binding().itemSendAnalytics.apply {
			valueSwitch = { prefs.getSendAnalytics() }
			onTapped {
				prefs.putSendAnalytics(!prefs.getSendAnalytics())
				FirebaseCrashlytics.getInstance()
					.setCrashlyticsCollectionEnabled(prefs.getSendAnalytics())
			}
		}

		binding().itemLogout.onTapped {
			DialogUtil.create(
				object : DialogUtil.StatefulDialog<DialogLoadingBinding>() {
					override fun toggle() {
						binding.progressBar.isVisible = true
						binding.messageMain.setText(R.string.loading)
						binding.messageSecondary.isGone = true
						binding.buttonPrimary.isGone = true
						binding.buttonSecondary.isGone = true
					}
				},
				requireContext(), DialogLoadingBinding::class.java, { dialog ->
					dialog.messageMain.setText(R.string.are_you_sure)
					dialog.messageSecondary.setText(R.string.logout_description)
					dialog.buttonPrimary.setText(R.string.logout)
					dialog.buttonPrimary.setOnClickListener {
						toggle()
						doAsync {
							Preferences.get().clear()
							AppDatabase.init(requireContext()).clearAllTables()
							this@create.dialog.dismiss()
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
					dialog.buttonSecondary.setOnClickListener { this.dialog.dismiss() }
				}
			)
		}
	}
}
