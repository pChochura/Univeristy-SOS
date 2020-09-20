package com.pointlessapps.mobileusos.helpers

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

object CustomTabsHelper {
	private const val STABLE_PACKAGE = "com.android.chrome"
	private const val BETA_PACKAGE = "com.chrome.beta"
	private const val DEV_PACKAGE = "com.chrome.dev"
	private const val LOCAL_PACKAGE = "com.google.android.apps.chrome"
	private const val ACTION_CUSTOM_TABS_CONNECTION =
		"android.support.customtabs.action.CustomTabsService"

	private var sPackageNameToUse: String? = null

	fun getPackageNameToUse(context: Context): String? {
		if (sPackageNameToUse != null) {
			return sPackageNameToUse
		}

		val activityIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.example.com"))
		val defaultViewHandlerPackageName =
			context.packageManager.resolveActivity(activityIntent, 0)?.run {
				activityInfo.packageName
			}

		val packagesSupportingCustomTabs = Intent().run {
			action = ACTION_CUSTOM_TABS_CONNECTION
			return@run context.packageManager.queryIntentActivities(activityIntent, 0)
				.filter { info ->
					setPackage(info.activityInfo.packageName)
					return@filter context.packageManager.resolveService(this, 0) != null
				}.map { it.activityInfo.packageName }
		}

		sPackageNameToUse = when {
			packagesSupportingCustomTabs.isEmpty() -> null
			packagesSupportingCustomTabs.size == 1 -> packagesSupportingCustomTabs[0]
			!defaultViewHandlerPackageName.isNullOrEmpty()
					&& !hasSpecializedHandlerIntents(context, activityIntent)
					&& packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName) -> defaultViewHandlerPackageName
			packagesSupportingCustomTabs.contains(STABLE_PACKAGE) -> STABLE_PACKAGE
			packagesSupportingCustomTabs.contains(BETA_PACKAGE) -> BETA_PACKAGE
			packagesSupportingCustomTabs.contains(DEV_PACKAGE) -> DEV_PACKAGE
			packagesSupportingCustomTabs.contains(LOCAL_PACKAGE) -> LOCAL_PACKAGE
			else -> null
		}

		return sPackageNameToUse
	}

	private fun hasSpecializedHandlerIntents(context: Context, intent: Intent) = try {
		context.packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER)
			.find { it.activityInfo != null && it.filter?.run { countDataAuthorities() != 0 && countDataTypes() != 0 } ?: false } != null
	} catch (e: RuntimeException) {
		false
	}
}
