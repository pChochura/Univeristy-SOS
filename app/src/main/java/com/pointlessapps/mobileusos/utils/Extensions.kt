package com.pointlessapps.mobileusos.utils

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.size
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.views.WeekView
import net.grandcentrix.tray.AppPreferences
import java.util.*

fun AppPreferences.putJson(key: String, obj: Any) = put(key, Gson().toJson(obj))

val Int.dp: Int
	get() = (this * Resources.getSystem().displayMetrics.density).toInt()

inline fun <reified T> AppPreferences.getJson(key: String): T? =
	Gson().fromJson(getString(key, ""), T::class.java)

inline fun <reified T> Gson.fromJson(json: String): T =
	this.fromJson(json, object : TypeToken<T>() {}.type)

fun WeekView.WeekViewEvent.getMonthKey() =
	Utils.monthKey(startTime.get(Calendar.MONTH), startTime.get(Calendar.YEAR))

fun Calendar.getDayKey() =
	Utils.dayKey(get(Calendar.DAY_OF_YEAR), get(Calendar.MONTH), get(Calendar.YEAR))

fun Calendar.forEachDays(days: Int, function: (Calendar) -> Unit) = repeat(days) {
	add(Calendar.DAY_OF_YEAR, 1)
	function(this)
}

fun String.capitalize() = "${this[0].toUpperCase()}${this.substring(1)}"

fun Int.toMB() = this / (1024f * 1024f) * 10f / 10f

fun ViewGroup.addChip(chipText: String, onRemovedListener: (() -> Unit)? = null) =
	addView(
		(LayoutInflater.from(context)
			.inflate(R.layout.list_item_email_recipient, null) as? Chip)?.apply {
			text = chipText
			setOnCloseIconClickListener {
				removeView(this)
				onRemovedListener?.invoke()
			}
		}, size - 1
	)
