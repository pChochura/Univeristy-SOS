package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersChapters {

	@TypeConverter
	fun toPages(json: String): List<Chapter.Page>? = Gson().fromJson(json)
}
