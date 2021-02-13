package com.pointlessapps.mobileusos.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.clients.ClientUSOS
import com.pointlessapps.mobileusos.models.University
import org.jetbrains.anko.doAsync

object HelperClientUSOS {

	const val CALLBACK_URL_HOST = "usosauth"
	var university: University? = null

	private const val LOGIN_TABS_REQUEST_CODE = 123
	private var requestToken: OAuth1RequestToken? = null
	private var service: OAuth10aService? = null

	fun handleLogin(
		activity: Activity,
		university: University,
		authUrlCallback: ((String) -> Unit)? = null
	) {
		doAsync {
			service =
				getService(university.url, university.consumerKey!!, university.consumerSecret!!)
			service?.apply {
				this@HelperClientUSOS.university = university
				this@HelperClientUSOS.requestToken = requestToken

				authUrlCallback?.also {
					it(getAuthorizationUrl(this@HelperClientUSOS.requestToken))

					return@apply
				}

				activity.startActivityForResult(CustomTabsIntent.Builder().apply {
					setDefaultColorSchemeParams(
						CustomTabColorSchemeParams.Builder()
							.setToolbarColor(
								ContextCompat.getColor(
									activity,
									R.color.colorPrimary
								)
							)
							.build()
					)
				}.build().intent.apply {
					setPackage(CustomTabsHelper.getPackageNameToUse(activity))
					data = Uri.parse(getAuthorizationUrl(this@HelperClientUSOS.requestToken))
					addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
				}, LOGIN_TABS_REQUEST_CODE)
			}
		}
	}

	fun handleLoginResult(activity: Activity, data: Uri?, successCallback: () -> Unit) {
		activity.finishActivity(LOGIN_TABS_REQUEST_CODE)
		if (data?.scheme == CALLBACK_URL_HOST) {
			doAsync {
				val verifier = data.getQueryParameter("oauth_verifier")
				val accessToken: OAuth1AccessToken =
					service?.getAccessToken(requestToken, verifier)
						?: throw NullPointerException("oauthService cannot be null.")
				Preferences.get().putAccessToken(accessToken)
				Preferences.get().putSelectedUniversity(this@HelperClientUSOS.university!!)
				successCallback.invoke()
			}
		}
	}

	fun isLoggedIn() = Preferences.get().getAccessToken() != null

	fun getService(baseUrl: String, consumerKey: String, consumerSecret: String): OAuth10aService =
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
					"offline_access",
					"mailclient",
					"surveys_filling",
					"crstests"
				)
			)
}
