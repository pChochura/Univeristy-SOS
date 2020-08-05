package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersNews {

	@TypeConverter
	fun fromEvent(event: Article.Event?): String = Gson().toJson(event)

	@TypeConverter
	fun fromCategory(category: Article.Category?): String = Gson().toJson(category)

	@TypeConverter
	fun toEvent(json: String): Article.Event? = Gson().fromJson(json)

	@TypeConverter
	fun toCategory(json: String): Article.Category? = Gson().fromJson(json)
}
