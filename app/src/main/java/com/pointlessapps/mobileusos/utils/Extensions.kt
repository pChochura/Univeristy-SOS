package com.pointlessapps.mobileusos.utils

import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pointlessapps.mobileusos.views.WeekView
import net.grandcentrix.tray.AppPreferences
import java.util.*

fun AppPreferences.putJson(key: String, obj: Any) = put(key, Gson().toJson(obj))

inline fun <reified T> AppPreferences.getJson(key: String): T? =
	Gson().fromJson(getString(key, ""), T::class.java)

inline fun <reified T> Gson.fromJson(json: String): T =
	this.fromJson(json, object : TypeToken<T>() {}.type)

fun TabLayout.getTabs() = mutableListOf<TabLayout.Tab>().apply {
	for (i in 0 until tabCount) {
		add(getTabAt(i) ?: continue)
	}
}

fun <K, V> Map<K, V>.keyAt(position: Int) = keys.elementAt(position)

fun <K, V> Map<K, V>.valueAt(position: Int) = values.elementAt(position)

fun WeekView.WeekViewEvent.getMonthKey() =
	Utils.monthKey(startTime.get(Calendar.MONTH), startTime.get(Calendar.YEAR))

fun Calendar.getDayKey() =
	Utils.dayKey(get(Calendar.DAY_OF_YEAR), get(Calendar.MONTH), get(Calendar.YEAR))

fun Date.getDayKey() =
	Calendar.getInstance().apply { timeInMillis = this@getDayKey.time }.getDayKey()

fun Calendar.forEachDaysIndexed(days: Int, function: (Int, Calendar) -> Unit) = (1..days).forEach {
	add(Calendar.DAY_OF_YEAR, 1)
	function(it - 1, this)
}

fun Calendar.forEachDaysReverseIndexed(days: Int, function: (Int, Calendar) -> Unit) {
	add(Calendar.DAY_OF_YEAR, days)
	repeat(days) {
		add(Calendar.DAY_OF_YEAR, -1)
		function(it, this)
	}
}