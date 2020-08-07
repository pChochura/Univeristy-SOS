package com.pointlessapps.mobileusos.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.fromJson

class ConvertersEmail {

	@TypeConverter
	fun fromAttachments(attachments: List<Email.Attachment>?): String = Gson().toJson(attachments)

	@TypeConverter
	fun toAttachments(json: String): List<Email.Attachment>? = Gson().fromJson(json)
}
