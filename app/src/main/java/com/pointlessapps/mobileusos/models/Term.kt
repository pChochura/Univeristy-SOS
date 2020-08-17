package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_terms")
@Keep
data class Term(
	@PrimaryKey
	var id: String = UUID.randomUUID().toString(),
	var name: Name? = null,
	@ColumnInfo(name = "start_date")
	var startDate: Date? = null,
	@ColumnInfo(name = "end_date")
	var endDate: Date? = null,
	@ColumnInfo(name = "order_key")
	var orderKey: Long? = null
) : Comparable<Term?> {

	override fun compareTo(other: Term?) =
		compareValuesBy(
			other,
			this,
			{ it?.orderKey },
			{ it?.startDate },
			{ it?.endDate },
			{ it?.id })

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Term

		if (id != other.id) return false

		return true
	}

	override fun hashCode() = id.hashCode()

	fun set(term: Term?) {
		id = term?.id ?: id
		name = term?.name ?: name
		startDate = term?.startDate ?: startDate
		endDate = term?.endDate ?: endDate
		orderKey = term?.orderKey ?: orderKey
	}
}
