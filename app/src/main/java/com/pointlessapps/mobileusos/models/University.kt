package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_universities")
@Keep
data class University(
	@PrimaryKey
	@SerializedName("url")
	var url: String = UUID.randomUUID().toString(),
	@SerializedName("name")
	var name: String? = null,
	@SerializedName("location")
	var location: String? = null,
	@ColumnInfo(name = "consumer_key")
	@SerializedName("consumer_key")
	var consumerKey: String? = null,
	@ColumnInfo(name = "consumer_secret")
	@SerializedName("consumer_secret")
	var consumerSecret: String? = null,
	@ColumnInfo(name = "service_url")
	@SerializedName("service_url")
	var serviceUrl: String? = null,
	@SerializedName("available")
	var available: Boolean = true
) : Comparable<University> {

	fun matches(text: String) = text.trim().split(" ").all {
		name?.contains(it, true) ?: false || location?.contains(it, true) ?: false
	}

	override fun compareTo(other: University) =
		compareValuesBy(this, other, { it.location }, { it.name })
}
