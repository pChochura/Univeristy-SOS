package com.pointlessapps.mobileusos.utils

import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.grandcentrix.tray.AppPreferences

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