package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_emails")
@Keep
data class Email(
	@SerializedName("attachments")
	var attachments: List<Attachment>? = null,
	@SerializedName("content")
	var content: String? = null,
	@SerializedName("date")
	var date: Date? = null,
	@SerializedName("status")
	var status: String? = null,
	@SerializedName("subject")
	var subject: String? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) : Comparable<Email> {

	override fun compareTo(other: Email) = compareValuesBy(this, other, { date?.time }, { subject })

	@Keep
	data class Attachment(
		@SerializedName("id")
		var id: String,
		@SerializedName("filename")
		var filename: String? = null,
		@SerializedName("size")
		var size: Int? = null,
		@SerializedName("description")
		var description: String,
		@SerializedName("url")
		var url: String
	) : Comparable<Attachment> {
		override fun compareTo(other: Attachment) =
			compareValuesBy(this, other, { filename }, { size })
	}

	@Keep
	data class Recipient(
		@SerializedName("email")
		var email: String?,
		@SerializedName("user")
		var user: User?
	) {
		fun name() = if (user != null) user?.name()!! else email!!
	}
}
