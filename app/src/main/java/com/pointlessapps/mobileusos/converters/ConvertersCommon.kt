package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson

class ConvertersCommon {

	@TypeConverter
	fun toString(title: Any?): String = Gson().toJson(title)
}