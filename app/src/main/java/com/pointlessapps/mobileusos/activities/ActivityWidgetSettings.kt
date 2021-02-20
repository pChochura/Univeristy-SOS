package com.pointlessapps.mobileusos.activities

import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterCore
import com.pointlessapps.mobileusos.databinding.*
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode
import com.pointlessapps.mobileusos.helpers.getWidgetConfiguration
import com.pointlessapps.mobileusos.helpers.putWidgetConfiguration
import com.pointlessapps.mobileusos.models.WidgetConfiguration
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import com.pointlessapps.mobileusos.views.SettingsItemGroup
import com.pointlessapps.mobileusos.views.WeekView
import com.pointlessapps.mobileusos.widgets.WidgetTimetable
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*

class ActivityWidgetSettings : AppCompatActivity() {

	private lateinit var binding: ActivityWidgetSettingsBinding

	private lateinit var widgetConfiguration: WidgetConfiguration

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Preferences.init(applicationContext)
		setTheme(
			if (Preferences.get().getSystemDarkMode()) R.style.AppTheme_Dark else R.style.AppTheme
		)

		binding = ActivityWidgetSettingsBinding.inflate(layoutInflater)
		setContentView(binding.root)

		widgetConfiguration = Preferences.get().getWidgetConfiguration()

		val widgetId = intent.getIntExtra(
			AppWidgetManager.EXTRA_APPWIDGET_ID,
			AppWidgetManager.INVALID_APPWIDGET_ID
		)

		setResult(
			RESULT_CANCELED,
			Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
		)

		prepareSettings()
		prepareSettingsChangeListener()

		binding.imageWidget.post { drawWidget() }
		binding.buttonSave.setOnClickListener {
			Preferences.get().putWidgetConfiguration(widgetConfiguration)
			val ids = if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
				AppWidgetManager.getInstance(applicationContext).getAppWidgetIds(
					ComponentName(
						applicationContext,
						WidgetTimetable::class.java
					)
				)
			} else {
				intArrayOf(widgetId)
			}
			sendBroadcast(
				Intent(
					AppWidgetManager.ACTION_APPWIDGET_UPDATE,
					null,
					this,
					WidgetTimetable::class.java
				).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
			)
			setResult(
				RESULT_OK,
				Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
			)
			finish()
		}
	}

	private fun prepareSettingsChangeListener() {
		binding.itemVisibleDays.apply {
			value = { widgetConfiguration.visibleDays.toString() }
			onTapped { item ->
				showDialogList(
					this@ActivityWidgetSettings,
					R.string.number_of_visible_days_title,
					(3..7).map(Int::toString).toList()
				) {
					widgetConfiguration.visibleDays = it.toInt()

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemVisibleHoursBefore.apply {
			value = { widgetConfiguration.visibleHoursBefore.toString() }
			onTapped { item ->
				showDialogList(
					this@ActivityWidgetSettings,
					R.string.number_of_visible_hours_before_title,
					(1..6).map(Int::toString)
				) {
					widgetConfiguration.visibleHoursBefore = it.toInt()

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemVisibleHoursAfter.apply {
			value = { widgetConfiguration.visibleHoursAfter.toString() }
			onTapped { item ->
				showDialogList(
					this@ActivityWidgetSettings,
					R.string.number_of_visible_hours_after_title,
					(1..6).map(Int::toString)
				) {
					widgetConfiguration.visibleHoursAfter = it.toInt()

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemTransparency.apply {
			value = { widgetConfiguration.transparency.toString() }
			onTapped { item ->
				showDialogSlider(
					this@ActivityWidgetSettings,
					R.string.transparency,
					0, 100, widgetConfiguration.transparency
				) {
					widgetConfiguration.transparency = it.toInt()

					drawWidget()
					item.refresh()
				}
			}
		}

		binding.itemFutureBackgroundColor.apply {
			valueColor = { widgetConfiguration.futureBackgroundColor }
			onTapped { item ->
				showDialogColorPicker(
					this@ActivityWidgetSettings,
					R.string.future_background_color
				) {
					widgetConfiguration.futureBackgroundColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemPastBackgroundColor.apply {
			valueColor = { widgetConfiguration.pastBackgroundColor }
			onTapped { item ->
				showDialogColorPicker(this@ActivityWidgetSettings, R.string.past_background_color) {
					widgetConfiguration.pastBackgroundColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemWeekendBackgroundColor.apply {
			valueColor = { widgetConfiguration.weekendsBackgroundColor }
			onTapped { item ->
				showDialogColorPicker(
					this@ActivityWidgetSettings,
					R.string.weekends_background_color
				) {
					widgetConfiguration.weekendsBackgroundColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemNowLineColor.apply {
			valueColor = { widgetConfiguration.nowLineColor }
			onTapped { item ->
				showDialogColorPicker(this@ActivityWidgetSettings, R.string.now_line_color) {
					widgetConfiguration.nowLineColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemTodayHeaderTextColor.apply {
			valueColor = { widgetConfiguration.todayHeaderTextColor }
			onTapped { item ->
				showDialogColorPicker(
					this@ActivityWidgetSettings,
					R.string.today_header_text_color
				) {
					widgetConfiguration.todayHeaderTextColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemHeaderTextColor.apply {
			valueColor = { widgetConfiguration.headerTextColor }
			onTapped { item ->
				showDialogColorPicker(this@ActivityWidgetSettings, R.string.header_text_color) {
					widgetConfiguration.headerTextColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemHeaderBackgroundColor.apply {
			valueColor = { widgetConfiguration.headerBackgroundColor }
			onTapped { item ->
				showDialogColorPicker(
					this@ActivityWidgetSettings,
					R.string.header_background_color
				) {
					widgetConfiguration.headerBackgroundColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemDividerLineColor.apply {
			valueColor = { widgetConfiguration.dividerLineColor }
			onTapped { item ->
				showDialogColorPicker(this@ActivityWidgetSettings, R.string.divider_line_color) {
					widgetConfiguration.dividerLineColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemEventTextColor.apply {
			valueColor = { widgetConfiguration.eventTextColor }
			onTapped { item ->
				showDialogColorPicker(this@ActivityWidgetSettings, R.string.event_text_color) {
					widgetConfiguration.eventTextColor = it

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemEventTextSize.apply {
			value = { widgetConfiguration.eventTextSize.toString() }
			onTapped { item ->
				showDialogSlider(
					this@ActivityWidgetSettings,
					R.string.event_text_size,
					12,
					20,
					widgetConfiguration.eventTextSize
				) {
					widgetConfiguration.eventTextSize = it.toInt()

					drawWidget()
					item.refresh()
				}
			}
		}
		binding.itemResetSettings.onTapped {
			widgetConfiguration = WidgetConfiguration()
			prepareSettings()
			drawWidget()
			binding.containerSettings.forEach {
				if (it is SettingsItemGroup) {
					it.refreshItems()
				}
			}
		}
	}

	private fun prepareSettings() {
		if (ColorUtils.setAlphaComponent(widgetConfiguration.futureBackgroundColor, 0) == 0) {
			widgetConfiguration.futureBackgroundColor =
				themeColor(R.attr.weekViewHeaderBackgroundColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.pastBackgroundColor, 0) == 0) {
			widgetConfiguration.pastBackgroundColor = themeColor(R.attr.weekViewPastBackgroundColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.weekendsBackgroundColor, 0) == 0) {
			widgetConfiguration.weekendsBackgroundColor =
				themeColor(R.attr.weekViewWeekendBackgroundColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.nowLineColor, 0) == 0) {
			widgetConfiguration.nowLineColor =
				ContextCompat.getColor(applicationContext, R.color.colorAccent)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.todayHeaderTextColor, 0) == 0) {
			widgetConfiguration.todayHeaderTextColor =
				ContextCompat.getColor(applicationContext, R.color.colorAccent)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.headerTextColor, 0) == 0) {
			widgetConfiguration.headerTextColor = themeColor(R.attr.weekViewHeaderTextColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.headerBackgroundColor, 0) == 0) {
			widgetConfiguration.headerBackgroundColor =
				themeColor(R.attr.weekViewHeaderBackgroundColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.dividerLineColor, 0) == 0) {
			widgetConfiguration.dividerLineColor = themeColor(R.attr.weekViewDividerLineColor)
		}
		if (ColorUtils.setAlphaComponent(widgetConfiguration.eventTextColor, 0) == 0) {
			widgetConfiguration.eventTextColor = themeColor(R.attr.weekViewEventTextColor)
		}
	}

	private fun drawWidget() {
		val calendar = Calendar.getInstance().apply {
			set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY - widgetConfiguration.visibleDays + 1)
		}
		val maxHour = max(min(calendar.get(Calendar.HOUR_OF_DAY), 20), 2)
		binding.imageWidget.setImageBitmap(WidgetTimetable.getWidgetBitmap(
			applicationContext,
			binding.imageWidget.width,
			binding.imageWidget.height,
			widgetConfiguration,
			calendar,
			listOf(
				WeekView.WeekViewEvent(
					1,
					"Event 1",
					(calendar.clone() as Calendar).apply {
						set(Calendar.HOUR_OF_DAY, maxHour - 1)
						set(Calendar.MINUTE, 0)
					},
					(calendar.clone() as Calendar).apply {
						set(Calendar.HOUR_OF_DAY, maxHour + 2)
						set(Calendar.MINUTE, 30)
					},
					null
				).apply {
					color = ContextCompat.getColor(applicationContext, R.color.color3)
				},
				WeekView.WeekViewEvent(
					2,
					"Event 2",
					(calendar.clone() as Calendar).apply {
						add(Calendar.DAY_OF_MONTH, 1)
						set(Calendar.HOUR_OF_DAY, maxHour)
						set(Calendar.MINUTE, 0)
					},
					(calendar.clone() as Calendar).apply {
						set(Calendar.HOUR_OF_DAY, maxHour + 1)
						set(Calendar.MINUTE, 45)
					},
					null
				).apply {
					color = ContextCompat.getColor(applicationContext, R.color.color2)
				}
			)))
	}

	companion object {
		fun showDialogSlider(
			context: Context,
			@StringRes title: Int,
			minValue: Int,
			maxValue: Int,
			value: Int,
			onSelectedListener: (String) -> Unit
		) {
			DialogUtil.create(
				context,
				DialogSliderBinding::class.java,
				{ dialog ->
					dialog.title.setText(title)
					dialog.slider.valueFrom = minValue.toFloat()
					dialog.slider.valueTo = maxValue.toFloat()
					dialog.slider.value = value.toFloat()
					dialog.buttonPrimary.setOnClickListener {
						onSelectedListener(dialog.slider.value.toInt().toString())
						dismiss()
					}
				})
		}

		fun showDialogList(
			context: Context,
			@StringRes title: Int,
			values: List<String>,
			onSelectedListener: (String) -> Unit
		) {
			showDialog(
				context,
				ListItemSimpleBinding::class.java,
				title,
				values,
				LinearLayoutManager(context),
				{ binding, value -> binding.root.text = value }
			) {
				onSelectedListener(it)
				dismiss()
			}
		}

		fun showDialogColorPicker(
			context: Context,
			@StringRes title: Int,
			onSelectedListener: (Int) -> Unit
		) {
			showDialog(
				context,
				ListItemColorBinding::class.java,
				title,
				context.resources.getIntArray(R.array.colors).map(Int::toString).toList(),
				GridLayoutManager(context, 5),
				{ binding, value -> binding.root.setColorFilter(value.toInt()) }
			) {
				onSelectedListener(it.toInt())
				dismiss()
			}
		}

		private fun <T : ViewBinding> showDialog(
			context: Context,
			bindingClass: Class<T>,
			@StringRes title: Int,
			values: List<String>,
			layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
			onBindListener: (T, String) -> Unit,
			onSelectedListener: Dialog.(String) -> Unit
		) {
			DialogUtil.create(
				context,
				DialogListPickerBinding::class.java,
				{ dialog ->
					dialog.title.setText(title)
					dialog.listItems.apply {
						adapter = object :
							AdapterCore<String, T>(values.toMutableList(), bindingClass) {
							override fun onBind(binding: T, position: Int) {
								binding.root.apply {
									onBindListener(binding, list[position])
									setOnClickListener { onSelectedListener(list[position]) }
								}
							}
						}
						this.layoutManager = layoutManager
					}
				})
		}
	}
}