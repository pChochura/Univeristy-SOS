package com.pointlessapps.mobileusos.services

import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Response
import com.github.scribejava.core.oauth.OAuth10aService
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getAccessToken
import com.pointlessapps.mobileusos.helpers.getSelectedUniversity
import com.pointlessapps.mobileusos.utils.Utils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

open class USOSApi {

	private val accessToken = Preferences.get().getAccessToken()
	private val service: OAuth10aService by lazy {
		selectedUniversity.run {
			HelperClientUSOS.getService(url, consumerKey!!, consumerSecret!!)
		}
	}

	protected val selectedUniversity = Preferences.get().getSelectedUniversity()
		?: throw NullPointerException("Any university was selected!")

	protected val dateFormat: DateFormat by lazy {
		SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
	}

	val gson: Gson by lazy {
		GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.create()
	}

	fun execute(request: OAuthRequest): Response? = service.run {
		signRequest(accessToken, request)
		execute(request)
	}
}