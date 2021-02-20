package com.pointlessapps.mobileusos.helpers

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
	fun withDefaultLocale(context: Context): Context =
		context.createConfigurationContext(Configuration(context.resources.configuration.apply {
			setLocale(
				Locale.forLanguageTag(
					Preferences.init(context).get().getSystemDefaultLanguage()
						?.toLowerCase(Locale.getDefault())
						?: return context
				).also { Locale.setDefault(it) }
			)
		}))

	fun withLocale(context: Context, localeTag: String): Context =
		context.createConfigurationContext(Configuration(context.resources.configuration.apply {
			setLocale(Locale.forLanguageTag(localeTag))
		}))
}
