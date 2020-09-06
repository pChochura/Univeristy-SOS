package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
class Name(
	@SerializedName("pl")
	private var pl: String? = null,
	@SerializedName("en")
	var en: String? = null
) : Comparable<Name> {

	override fun toString() =
		if (Locale.getDefault() == Locale.forLanguageTag("pl") || en.isNullOrBlank()) pl!! else en!!

	override fun compareTo(other: Name) = compareValuesBy(this, other, { it.toString() })

	fun isEmpty() = pl.isNullOrBlank() && en.isNullOrBlank()
}
