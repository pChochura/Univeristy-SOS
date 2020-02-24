package com.pointlessapps.mobileusos.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.grandcentrix.tray.AppPreferences

fun AppPreferences.putJson(key: String, obj: Any) = put(key, Gson().toJson(obj))

inline fun <reified T> AppPreferences.getJson(key: String): T? =
	Gson().fromJson(getString(key, ""), T::class.java)

inline fun <reified T> Gson.fromJson(json: String): T =
	this.fromJson(json, object : TypeToken<T>() {}.type)