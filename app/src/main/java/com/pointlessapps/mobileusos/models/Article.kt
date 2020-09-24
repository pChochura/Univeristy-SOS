package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_news")
@Keep
data class Article(
	@ColumnInfo(name = "publication_date")
	@SerializedName("publication_date")
	var publicationDate: Date? = null,
	@SerializedName("title")
	var title: Name? = null,
	@ColumnInfo(name = "headline_html")
	@SerializedName("headline_html")
	var headlineHtml: Name? = null,
	@ColumnInfo(name = "content_html")
	@SerializedName("content_html")
	var contentHtml: Name? = null,
	@SerializedName("event")
	var event: Event? = null,
	@SerializedName("category")
	@Embedded()
	var category: Category? = null,
	@SerializedName("author")
	var author: String? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) {
	@Keep
	data class Event(
		@SerializedName("start_date")
		var startDate: Date?,
		@SerializedName("end_date")
		var endDate: Date?,
		@SerializedName("city")
		val city: String,
		@SerializedName("address")
		val address: String
	)

	@Keep
	data class Category(
		@SerializedName("id")
		@ColumnInfo(name = "category_id")
		var id: String,
		@ColumnInfo(name = "category_name")
		@SerializedName("name")
		var name: Name?
	) : Comparable<Category> {
		override fun compareTo(other: Category) =
			compareValuesBy(this, other, { it.name.toString() })
	}
}
