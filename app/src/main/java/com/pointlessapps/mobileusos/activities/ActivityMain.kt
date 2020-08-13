package com.pointlessapps.mobileusos.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode
import com.pointlessapps.mobileusos.helpers.getSystemDefaultTab
import com.pointlessapps.mobileusos.services.FragmentManager

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

	override fun onBackPressed() {
		if (!fragmentManager.popHistory()) {
			super.onBackPressed()
		}
	}
}
