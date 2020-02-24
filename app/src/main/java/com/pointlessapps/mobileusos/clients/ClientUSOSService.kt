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

	fun userGroupsRequest() = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity.serviceUrl}/groups/user").buildUpon()
			.appendQueryParameter(
				"fields",
				"term_id|course_id|class_type|course_name|group_number|lecturers"
			)
			.appendQueryParameter("active_terms", "true")
			.build().toString()
	)

	fun termsRequest(termIds: List<String>) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity.serviceUrl}/terms/terms").buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|order_key|start_date|end_date"
			)
			.appendQueryParameter("term_ids", termIds.joinToString("|"))
			.build().toString()
	)
}