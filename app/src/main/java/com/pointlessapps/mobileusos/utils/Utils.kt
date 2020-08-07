package com.pointlessapps.mobileusos.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.Window
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.CourseEvent
import org.jetbrains.anko.find
import java.util.*


object Utils {

	fun getKeyboardHeight(window: Window, callback: (Int) -> Unit) {
		window.decorView.find<View>(android.R.id.content).apply {
			viewTreeObserver.addOnGlobalLayoutListener {
				val r = Rect()
				window.decorView.getWindowVisibleDisplayFrame(r)
				callback(height - r.bottom)
			}
		}
	}

	fun monthKey(month: Int, year: Int) =
		(if (month < 10) "0" else "") + "$month.$year"

	fun dayKey(day: Int, month: Int, year: Int) =
		(if (month < 10) "0" else "") + "$day." + (if (month < 10) "0" else "") + "$month.$year"

	fun getScreenSize() =
		Point(
			Resources.getSystem().displayMetrics.widthPixels,
			Resources.getSystem().displayMetrics.heightPixels
		)

	fun getColorByClassType(classType: String) = when (classType.toLowerCase(Locale.getDefault())) {
		"wyk" -> Color.parseColor("#FF80CBC4")
		"ćw" -> Color.parseColor("#FFF06292")
		"lab" -> Color.parseColor("#FFFF9E80")
		"wf" -> Color.parseColor("#FF81C784")
		else -> Color.parseColor("#FFAED581")
	}

	fun calendarIntent(context: Context, event: CourseEvent) {
		val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
			.setData(CalendarContract.Events.CONTENT_URI)
			.putExtra(CalendarContract.Events.TITLE, event.courseName.toString())
			.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
			.putExtra(
				CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				event.startTime.time
			)
			.putExtra(
				CalendarContract.EXTRA_EVENT_END_TIME,
				event.endTime?.time
			)
			.putExtra(
				CalendarContract.Events.EVENT_LOCATION,
				"${event.buildingName.toString()}, ${event.roomNumber}"
			)
			.putExtra(
				CalendarContract.Events.DESCRIPTION,
				context.getString(R.string.calendar_event_description)
			)
			.putExtra(
				CalendarContract.Events.CALENDAR_COLOR,
				getColorByClassType(event.classtypeId ?: "")
			)
			.putExtra(
				CalendarContract.Events.ACCESS_LEVEL,
				CalendarContract.Events.ACCESS_DEFAULT
			)
			.putExtra(
				CalendarContract.Events.AVAILABILITY,
				CalendarContract.Events.AVAILABILITY_BUSY
			)
		context.startActivity(
			Intent.createChooser(
				insertCalendarIntent,
				context.getString(R.string.create_event_title)
			)
		)
	}

	fun phoneIntent(context: Context, phoneNumber: String) {
		context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
	}

	fun mapsIntent(
		context: Context,
		lat: Float?,
		long: Float?,
		name: String?
	) {
		val gmmIntentUri = Uri.parse("geo:$lat,$long?q=${Uri.encode(name)}")
		val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
		mapIntent.setPackage("com.google.android.apps.maps")
		mapIntent.resolveActivity(context.packageManager ?: return)
			?.let {
				context.startActivity(mapIntent)
			}
	}

	fun parseHtml(input: String): Spanned =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Html.fromHtml(input, Html.FROM_HTML_MODE_COMPACT)
		} else {
			Html.fromHtml(input)
		}

	open class SingletonHolder<T : Any, in A>(creator: (A?) -> T) {
		private var creator: ((A?) -> T)? = creator

		@Volatile
		protected var instance: T? = null

		fun init(arg: A? = null): T {
			if (instance != null) return instance!!

			return synchronized(this) {
				if (instance != null) instance!!
				else {
					val created = creator!!(arg)
					instance = created
					creator = null
					created
				}
			}
		}
	}
}
