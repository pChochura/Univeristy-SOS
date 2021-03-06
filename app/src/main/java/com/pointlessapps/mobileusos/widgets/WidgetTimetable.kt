package com.pointlessapps.mobileusos.widgets

import android.app.Activity
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.activities.ActivityWidgetSettings
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getWidgetConfiguration
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.models.WidgetConfiguration
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.utils.px
import com.pointlessapps.mobileusos.views.WeekView
import java.util.*

class WidgetTimetable : AppWidgetProvider() {

	private val timetableEvents = mutableListOf<WeekView.WeekViewEvent>()

	private fun updateWidgetById(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetId: Int
	) {
		Preferences.init(context)
		if (!HelperClientUSOS.isLoggedIn()) {
			val views = RemoteViews(context.packageName, R.layout.widget_timetable_require_login)
			views.setOnClickPendingIntent(
				R.id.container,
				PendingIntent.getActivity(
					context,
					0,
					Intent(context, ActivityLogin::class.java),
					PendingIntent.FLAG_UPDATE_CURRENT
				)
			)
			appWidgetManager.updateAppWidget(appWidgetId, views)

			return
		}

		val (width, height) = getSizeConsideringOrientation(
			context,
			appWidgetManager.getAppWidgetOptions(appWidgetId)
		)
		val calendar = Calendar.getInstance().apply {
			set(Calendar.SECOND, 0)
			set(Calendar.MINUTE, 0)
			set(Calendar.HOUR_OF_DAY, 1)
		}
		val widgetConfiguration = Preferences.get().getWidgetConfiguration()
		RepositoryTimetable(context).getForDays(calendar, widgetConfiguration.visibleDays)
			.onOnceCallback {
				val set = timetableEvents.toMutableSet()
				set.addAll(it.first.filterNotNull().map(CourseEvent::toWeekViewEvent))
				timetableEvents.clear()
				timetableEvents.addAll(set)
			}
			.onFinished {
				val views = RemoteViews(context.packageName, R.layout.widget_timetable)
				views.setImageViewBitmap(
					R.id.imageWidget,
					getWidgetBitmap(
						context,
						width,
						height,
						widgetConfiguration,
						Calendar.getInstance(),
						timetableEvents
					)
				)

				views.setOnClickPendingIntent(
					R.id.buttonSettings,
					PendingIntent.getActivity(
						context,
						1,
						Intent(context, ActivityWidgetSettings::class.java),
						PendingIntent.FLAG_UPDATE_CURRENT
					)
				)

				views.setOnClickPendingIntent(
					R.id.imageWidget,
					PendingIntent.getActivity(
						context,
						0,
						Intent(context, ActivityLogin::class.java),
						PendingIntent.FLAG_UPDATE_CURRENT
					)
				)
				appWidgetManager.updateAppWidget(appWidgetId, views)
			}
	}

	override fun onUpdate(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetIds: IntArray
	) = appWidgetIds.forEach { updateWidgetById(context, appWidgetManager, it) }

	override fun onAppWidgetOptionsChanged(
		context: Context,
		appWidgetManager: AppWidgetManager,
		appWidgetId: Int,
		newOptions: Bundle?
	) = onUpdate(context, appWidgetManager, intArrayOf(appWidgetId))

	private fun getSizeConsideringOrientation(context: Context, bundle: Bundle) =
		when (context.resources.configuration.orientation) {
			Configuration.ORIENTATION_PORTRAIT -> intArrayOf(
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH).px,
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT).px
			)
			Configuration.ORIENTATION_LANDSCAPE -> intArrayOf(
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH).px,
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT).px
			)
			else -> intArrayOf(
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH).px,
				bundle.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT).px
			)
		}

	companion object {
		fun getWidgetId(context: Context): IntArray =
			AppWidgetManager.getInstance(context).getAppWidgetIds(
				ComponentName(context, WidgetTimetable::class.java)
			)

		fun requestRefresh(activity: Activity, ids: IntArray = getWidgetId(activity)) {
			activity.sendBroadcast(
				Intent(
					AppWidgetManager.ACTION_APPWIDGET_UPDATE,
					null,
					activity,
					WidgetTimetable::class.java
				).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
			)
		}

		fun getWidgetBitmap(
			context: Context,
			width: Int,
			height: Int,
			configuration: WidgetConfiguration,
			date: Calendar,
			eventsList: List<WeekView.WeekViewEvent>
		): Bitmap? {
			val view = WeekView(context)
			view.measure(
				View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
			)
			view.layout(0, 0, width, height)
			view.setHourDelta(configuration.visibleHoursBefore, configuration.visibleHoursAfter)
			view.setVisibleDays(configuration.visibleDays)
			view.setFutureBackgroundColor(configuration.futureBackgroundColor)
			view.setPastBackgroundColor(configuration.pastBackgroundColor)
			view.setEventTextSize(configuration.eventTextSize)
			view.setEventTextColor(configuration.eventTextColor)
			view.setWeekendBackgroundColor(configuration.weekendsBackgroundColor)
			view.setCurrentTimeLineColor(configuration.nowLineColor)
			view.setTodayTextColor(configuration.todayHeaderTextColor)
			view.setHeaderTextColor(configuration.headerTextColor)
			view.setHeaderColor(configuration.headerBackgroundColor)
			view.setDividerLineColor(configuration.dividerLineColor)
			view.scaleToFit(width.toFloat(), height.toFloat())
			view.setToday(date.clone() as Calendar)
			view.setMonthChangeListener { _, newMonth ->
				if (newMonth != date.get(Calendar.MONTH)) {
					return@setMonthChangeListener listOf()
				}

				eventsList
			}

			val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
			val canvas = Canvas(bitmap)
			canvas.clipPath(Path().apply {
				addRoundRect(
					0f,
					0f,
					width.toFloat(),
					height.toFloat(),
					5.px.toFloat(),
					5.px.toFloat(),
					Path.Direction.CW
				)
			})
			view.draw(canvas)
			return bitmap
		}
	}
}