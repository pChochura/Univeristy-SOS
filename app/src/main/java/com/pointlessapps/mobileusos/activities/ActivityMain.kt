package com.pointlessapps.mobileusos.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.services.FragmentManager

class ActivityMain : FragmentActivity() {

	private lateinit var fragmentManager: FragmentManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		fragmentManager = FragmentManager.of(
			this,
			FragmentTimetable(),
			FragmentCalendar(),
			FragmentMail(),
			FragmentNews(),
			FragmentProfile()
		).apply {
			showIn(R.id.containerFragment)
			selectFirst()
		}
	}

	override fun onBackPressed() {
		if (!fragmentManager.popHistory()) {
			super.onBackPressed()
		}
	}
}
