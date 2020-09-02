package com.pointlessapps.mobileusos.activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.LocaleHelper
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode
import com.pointlessapps.mobileusos.helpers.getSystemDefaultTab
import com.pointlessapps.mobileusos.managers.FragmentManager

class ActivityMain : FragmentActivity() {

	private lateinit var fragmentManager: FragmentManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (Preferences.get().getSystemDarkMode()) {
			setTheme(R.style.AppTheme_Dark)
		}

		setContentView(R.layout.activity_main)

		fragmentManager = FragmentManager.of(
			this,
			FragmentTimetable(),
			FragmentCalendar(),
			FragmentMails(),
			FragmentNews(),
			FragmentProfile()
		).apply {
			showIn(R.id.containerFragment)
			selectAt(Preferences.get().getSystemDefaultTab())
		}
	}

	override fun attachBaseContext(newBase: Context) {
		super.attachBaseContext(LocaleHelper.withLocale(newBase))
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		LocaleHelper.withLocale(this)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		fragmentManager.currentFragment?.handleOnActivityResult(requestCode, resultCode, data)
	}

	override fun onBackPressed() {
		if (!fragmentManager.popHistory()) {
			super.onBackPressed()
		}
	}
}
