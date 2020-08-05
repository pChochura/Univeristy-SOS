package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_news")
data class Article(
	@ColumnInfo(name = "publication_date")
	var publicationDate: Date? = null,
	var title: Name? = null,
	@ColumnInfo(name = "headline_html")
	var headlineHtml: Name? = null,
	@ColumnInfo(name = "content_html")
	var contentHtml: Name? = null,
	var event: Event? = null,
	var category: Category? = null,
	var author: String? = null,
	@PrimaryKey
	var id: String = ""
) {
	data class Event(
		var startDate: Date?,
		var endDate: Date?,
		val city: String,
		val address: String
	)

	data class Category(
		var id: String,
		var name: Name?
	)
}
