package com.pointlessapps.mobileusos.models

import java.util.*

class Name(private var pl: String? = null, var en: String? = null) : Comparable<Name> {

	override fun toString() =
		if (Locale.getDefault() == Locale.forLanguageTag("pl") || en.isNullOrBlank()) pl!! else en!!

	override fun compareTo(other: Name) = compareValuesBy(this, other, { it.toString() })
}
