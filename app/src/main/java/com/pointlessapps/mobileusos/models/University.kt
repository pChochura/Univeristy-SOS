package com.pointlessapps.mobileusos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_universities")
data class University(
	@PrimaryKey(autoGenerate = true)
	val id: Int? = null,
	var name: String? = null,
	var location: String? = null,
	var url: String? = null,
	var consumerKey: String? = null,
	var consumerSecret: String? = null
) : Comparable<University> {
	fun matches(text: String) = text.trim().split(" ").all {
		name?.contains(it, true) ?: false || location?.contains(it, true) ?: false
	}

	override fun compareTo(other: University) =
		compareValuesBy(this, other, { it.location }, { it.name })
}