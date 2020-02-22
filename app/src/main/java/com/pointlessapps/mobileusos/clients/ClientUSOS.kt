package com.pointlessapps.mobileusos.clients

import com.github.scribejava.core.builder.api.DefaultApi10a

class ClientUSOS(private val baseUrl: String, private val mScopes: Array<out String>) : DefaultApi10a() {

	override fun getAuthorizationBaseUrl() = "$baseUrl/services/oauth/authorize"

	override fun getRequestTokenEndpoint(): String {
		val tokenUrl = getRequestTokenUrl()
		return if (mScopes.isEmpty()) tokenUrl else "$tokenUrl?scopes=${mScopes.joinToString("|")}"
	}

	override fun getAccessTokenEndpoint(): String {
		return "$baseUrl/services/oauth/access_token"
	}

	private fun getBaseUrl(): String {
		return baseUrl
	}

	private fun getRequestTokenUrl(): String {
		return "${getBaseUrl()}/services/oauth/request_token"
	}

	companion object {
		fun withScopes(baseUrl: String, vararg scopes: String): ClientUSOS {
			return ClientUSOS(baseUrl, scopes)
		}
	}
}