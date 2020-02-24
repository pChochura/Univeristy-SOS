package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_terms")
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
) : Comparable<Term> {

	override fun compareTo(other: Term) =
		compareValuesBy(this, other, { it.orderKey }, { it.startDate }, { it.endDate })
}
