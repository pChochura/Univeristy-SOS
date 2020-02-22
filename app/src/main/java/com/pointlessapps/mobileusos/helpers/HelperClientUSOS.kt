package com.pointlessapps.mobileusos.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.clients.ClientUSOS
import com.pointlessapps.mobileusos.utils.getJson
import com.pointlessapps.mobileusos.utils.putJson
import org.jetbrains.anko.doAsync

object HelperClientUSOS {

	private const val LOGIN_TABS_REQUEST_CODE = 123
	private const val CALLBACK_URL_HOST = "usosauth"
	private var requestToken: OAuth1RequestToken? = null
	private var service: OAuth10aService? = null

	fun handleLogin(
		activity: Activity,
		baseUrl: String,
		consumerKey: String,
		consumerSecret: String
	) {
		doAsync {
			service = getService(baseUrl, consumerKey, consumerSecret)
			service?.apply {
				this@HelperClientUSOS.requestToken = requestToken

				activity.startActivityForResult(CustomTabsIntent.Builder().apply {
					setToolbarColor(
						ContextCompat.getColor(
							activity,
							R.color.colorPrimary
						)
					)
				}.build().intent.apply {
					data = Uri.parse(getAuthorizationUrl(this@HelperClientUSOS.requestToken))
					addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
				}, LOGIN_TABS_REQUEST_CODE)
			}
		}
	}

	fun handleLoginResult(activity: Activity, intent: Intent?, successCallback: () -> Unit) {
		activity.finishActivity(LOGIN_TABS_REQUEST_CODE)
		intent?.data?.also {
			if (it.scheme == CALLBACK_URL_HOST) {
				doAsync {
					val verifier = it.getQueryParameter("oauth_verifier")
					val accessToken: OAuth1AccessToken =
						service?.getAccessToken(requestToken, verifier)
							?: throw NullPointerException("oauthService cannot be null.")
					Preferences.get().putJson(Preferences.KEY_ACCESS_TOKEN, accessToken)
					successCallback.invoke()
				}
			}
		}
	}

	fun isLoggedIn() =
		Preferences.get().getJson<OAuth1AccessToken>(Preferences.KEY_ACCESS_TOKEN) != null

	private fun getService(baseUrl: String, consumerKey: String, consumerSecret: String) =
		ServiceBuilder(consumerKey)
			.apiSecret(consumerSecret)
			.callback("$CALLBACK_URL_HOST:///")
			.build(
				ClientUSOS.withScopes(
					baseUrl,
					"studies",
					"email",
					"other_emails",
					"grades",
					"offline_access"
				)
			)
}