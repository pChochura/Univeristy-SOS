package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Keep
@Entity(tableName = "table_chapters")
class Chapter(
	@ColumnInfo(name = "pages")
	@SerializedName("pages")
	val pages: List<Page>? = null,
	@ColumnInfo(name = "title")
	@SerializedName("title")
	val title: Name? = null,
	@PrimaryKey
	@ColumnInfo(name = "id")
	@SerializedName("id")
	val id: String = ""
) {

	@Keep
	class Page(
		@ColumnInfo(name = "entries")
		@SerializedName("entries")
		val entries: List<Entry>? = null,
		@ColumnInfo(name = "title")
		@SerializedName("title")
		val title: Name? = null,
		@PrimaryKey
		@ColumnInfo(name = "id")
		@SerializedName("id")
		val id: String = ""
	) {

		@Keep
		class Entry(
			@ColumnInfo(name = "image_urls")
			@SerializedName("image_urls")
			val imageUrls: Map<String, String>? = null,
			@ColumnInfo(name = "content")
			@SerializedName("content")
			val content: Name? = null,
			@ColumnInfo(name = "title")
			@SerializedName("title")
			val title: Name? = null,
			@PrimaryKey
			@ColumnInfo(name = "id")
			@SerializedName("id")
			val id: String = ""
		) : Comparable<Entry> {
			override fun compareTo(other: Entry) =
				compareValuesBy(this, other, { it.title.toString() })
		}
	}
}
