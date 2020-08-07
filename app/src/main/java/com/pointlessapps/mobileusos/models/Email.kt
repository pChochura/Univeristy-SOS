package com.pointlessapps.mobileusos.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_emails")
data class Email(
	var attachments: List<Attachment>? = null,
	var content: String? = null,
	var date: Date? = null,
	var status: String? = null,
	var subject: String? = null,
	@PrimaryKey
	var id: String = ""
) : Comparable<Email> {

	override fun compareTo(other: Email) = compareValuesBy(this, other, { date?.time }, { subject })

	data class Attachment(
		var id: String,
		var filename: String? = null,
		var size: Int? = null,
		var description: String,
		var url: String
	) : Comparable<Attachment> {
		override fun compareTo(other: Attachment) =
			compareValuesBy(this, other, { filename }, { size })
	}

	data class Recipient(var email: String?, var user: User?) {
		fun name() = if (user != null) user?.name()!! else email!!
	}
}
