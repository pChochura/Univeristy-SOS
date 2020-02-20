package com.pointlessapps.mobileusos.activities

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.fragments.FragmentProfile
import com.pointlessapps.mobileusos.fragments.FragmentSettings
import com.pointlessapps.mobileusos.fragments.FragmentTimetable
import com.pointlessapps.mobileusos.services.FragmentManager

class ActivityMain : FragmentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		FragmentManager.of(
			this,
			FragmentTimetable(),
			FragmentProfile(),
			FragmentSettings()
		).showIn(R.id.containerFragment)
	}
}