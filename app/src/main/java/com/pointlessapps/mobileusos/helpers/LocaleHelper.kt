package com.pointlessapps.mobileusos.helpers

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleHelper {
	fun withLocale(context: Context): Context {
		val locale = Locale.forLanguageTag(
			Preferences.get().getSystemDefaultLanguage()?.toLowerCase(Locale.getDefault())
				?: return context
		)

		Locale.setDefault(locale)
		return context.createConfigurationContext(Configuration(context.resources.configuration.apply {
			setLocale(locale)
		}))
	}
}
