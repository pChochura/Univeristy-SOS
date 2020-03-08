package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.utils.fromJson
import java.util.*

@TypeConverters
class ConvertersTerm {

	@TypeConverter
	fun toName(name: String): Name? = Gson().fromJson(name)

	@TypeConverter
	fun toDate(date: Long): Date? = Date(date)

	@TypeConverter
	fun toLong(date: Date): Long = date.time
}
