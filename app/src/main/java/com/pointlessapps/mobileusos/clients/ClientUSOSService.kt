package com.pointlessapps.mobileusos.clients

import android.net.Uri
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.pointlessapps.mobileusos.services.USOSApi
import com.pointlessapps.mobileusos.utils.Utils

class ClientUSOSService private constructor() : USOSApi() {

	companion object : Utils.SingletonHolder<ClientUSOSService, Unit>({ ClientUSOSService() })

	fun userDetailsRequest(userId: String?) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity.serviceUrl}/users/user").buildUpon().apply {
			if (userId != null) {
				appendQueryParameter("user_id", userId)
			}
			appendQueryParameter(
				"fields",
				"id|titles|email|first_name|last_name|student_number|photo_urls[200x200]"
			)
		}.build().toString()
	)
}