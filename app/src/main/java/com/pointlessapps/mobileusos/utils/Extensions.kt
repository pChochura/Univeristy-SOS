package com.pointlessapps.mobileusos.utils

import android.content.res.Resources
import android.util.Patterns
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.size
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pointlessapps.mobileusos.databinding.ListItemEmailRecipientBinding
import com.pointlessapps.mobileusos.views.WeekView
import net.grandcentrix.tray.AppPreferences
import java.util.*

fun AppPreferences.putJson(key: String, obj: Any) = put(key, Gson().toJson(obj))

val Int.dp: Int
	get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
	get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()

inline fun <reified T> AppPreferences.getJson(key: String, default: String = ""): T =
	Gson().fromJson(getString(key, default), T::class.java)

inline fun <reified T> Gson.fromJson(json: String): T =
	this.fromJson(json, object : TypeToken<T>() {}.type)

fun WeekView.WeekViewEvent.getMonthKey() =
	Utils.monthKey(startTime.get(Calendar.MONTH), startTime.get(Calendar.YEAR))

fun Calendar.getDayKey() =
	Utils.dayKey(get(Calendar.DAY_OF_MONTH), get(Calendar.MONTH), get(Calendar.YEAR))

fun Calendar.forEachDays(days: Int, function: (Calendar) -> Unit) = repeat(days) {
	add(Calendar.DAY_OF_YEAR, 1)
	function(this)
}

fun Long.toMB() = this / (1024f * 1024f) * 10f / 10f

fun ViewGroup.addChip(chipText: String, onRemovedListener: (() -> Unit)? = null) =
	addView(
		ListItemEmailRecipientBinding.inflate(
			LayoutInflater.from(context),
			null,
			false
		).root.apply {
			text = chipText
			setOnCloseIconClickListener {
				removeView(this)
				onRemovedListener?.invoke()
			}
		}, size - 1
	)

fun String.isEmailProtocol() =
	Patterns.EMAIL_ADDRESS.matcher(this.substringAfter("mailto:")).matches()

fun String.withoutExtension() = substringBeforeLast(".")

fun String.extension() = substringAfterLast(".", "")
