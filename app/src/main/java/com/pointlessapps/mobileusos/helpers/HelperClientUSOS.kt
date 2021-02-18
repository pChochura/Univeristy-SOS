package com.pointlessapps.mobileusos.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.github.scribejava.core.builder.ServiceBuilder
import com.github.scribejava.core.model.OAuth1AccessToken
import com.github.scribejava.core.model.OAuth1RequestToken
import com.github.scribejava.core.oauth.OAuth10aService
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityMain
import com.pointlessapps.mobileusos.clients.ClientUSOS
import com.pointlessapps.mobileusos.models.University
import org.jetbrains.anko.doAsync

object HelperClientUSOS {

	const val CALLBACK_URL_HOST = "usosauth"
	var university: University? = null

	private var requestToken: OAuth1RequestToken? = null
	private var service: OAuth10aService? = null
	private lateinit var resultLauncher: ActivityResultLauncher<Intent>

	fun registerActivityResultListener(activity: ComponentActivity) {
		resultLauncher =
			activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
				handleLoginResult(activity, it.data?.data)
			}
	}

	fun handleLogin(
		activity: ComponentActivity,
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

				launchUrl(activity, this)
			}
		}
	}

	private fun launchUrl(activity: Activity, service: OAuth10aService) {
		val intent = when {
			CustomTabsHelper.getPackageNameToUse(activity) != null -> CustomTabsIntent.Builder()
				.apply {
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
				}.build().intent.setPackage(CustomTabsHelper.getPackageNameToUse(activity))
			else -> Intent(Intent.ACTION_VIEW)
		}

		resultLauncher.launch(intent.apply {
			data = Uri.parse(service.getAuthorizationUrl(this@HelperClientUSOS.requestToken))
			addCategory(Intent.CATEGORY_BROWSABLE)
			addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
		})
	}

	fun handleLoginResult(activity: Activity, data: Uri?) {
		if (data?.scheme == CALLBACK_URL_HOST) {
			doAsync {
				val verifier = data.getQueryParameter("oauth_verifier")
				val accessToken: OAuth1AccessToken =
					service?.getAccessToken(requestToken, verifier)
						?: throw NullPointerException("oauthService cannot be null.")
				Preferences.get().putAccessToken(accessToken)
				Preferences.get().putSelectedUniversity(this@HelperClientUSOS.university!!)
				activity.startActivity(
					Intent(
						activity,
						ActivityMain::class.java
					).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
				)
			}
		}
	}

	fun isLoggedIn() = Preferences.get().getAccessToken() != null

	private fun prepareScopes() =
		mutableListOf("studies", "email", "grades", "offline_access").apply {
			val prefs = Preferences.get()
			if (prefs.getScopeOtherEmails()) add("other_emails")
			if (prefs.getScopeCrsTests()) add("crstests")
			if (prefs.getScopeMailClient()) add("mailclient")
			if (prefs.getScopeSurveyFilling()) add("surveys_filling")
			if (prefs.getScopeEvents()) add("events")
		}.toTypedArray()

	fun getService(baseUrl: String, consumerKey: String, consumerSecret: String): OAuth10aService =
		ServiceBuilder(consumerKey)
			.apiSecret(consumerSecret)
			.callback("$CALLBACK_URL_HOST:///")
			.build(ClientUSOS.withScopes(baseUrl, *prepareScopes()))
}
