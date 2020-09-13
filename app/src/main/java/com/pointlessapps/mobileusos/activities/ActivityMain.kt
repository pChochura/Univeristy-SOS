package com.pointlessapps.mobileusos.activities

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.LocaleHelper
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode
import com.pointlessapps.mobileusos.helpers.getSystemDefaultTab
import com.pointlessapps.mobileusos.managers.FragmentManager
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.contentView

class ActivityMain : FragmentActivity() {

	private lateinit var fragmentManager: FragmentManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setTheme(
			if (Preferences.get().getSystemDarkMode()) R.style.AppTheme_Dark else R.style.AppTheme
		)
		FirebaseApp.initializeApp(applicationContext)
		Preferences.init(applicationContext)

		setContentView(R.layout.activity_main)
		bg.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
		contentView?.rootView?.setBackgroundColor(themeColor(R.attr.colorBackground))

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

			intent.getStringExtra("destinationFragmentName")?.also {
				(Class.forName(it)?.newInstance() as? FragmentBase)?.also(::setFragment)
			}
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
