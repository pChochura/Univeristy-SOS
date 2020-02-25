package com.pointlessapps.mobileusos.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_universities")
data class University(
	@PrimaryKey
	var url: String = UUID.randomUUID().toString(),
	var name: String? = null,
	var location: String? = null,
	var consumerKey: String? = null,
	var consumerSecret: String? = null,
	var serviceUrl: String? = null,
	var available: Boolean = true
) : Comparable<University> {

	fun matches(text: String) = text.trim().split(" ").all {
		name?.contains(it, true) ?: false || location?.contains(it, true) ?: false
	}

	override fun compareTo(other: University) =
		compareValuesBy(this, other, { it.location }, { it.name })
}