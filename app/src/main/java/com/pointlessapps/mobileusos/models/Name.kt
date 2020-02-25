package com.pointlessapps.mobileusos.models

class Name : Comparable<Name> {
	var pl: String? = null
	var en: String? = null

	override fun toString() = pl!!

	override fun compareTo(other: Name) = compareValuesBy(this, other, { it.toString() })
}