package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ActivityLoginBinding
import com.pointlessapps.mobileusos.fragments.FragmentCoreImpl
import com.pointlessapps.mobileusos.fragments.FragmentLogin
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode

class ActivityLogin : FragmentActivity() {

	private val loginFragment = FragmentLogin()
	private var currentFragment: FragmentCoreImpl<*>? = loginFragment

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		FirebaseApp.initializeApp(applicationContext)
		Preferences.init(applicationContext)

		if (HelperClientUSOS.isLoggedIn()) {
			startActivity(Intent(this, ActivityMain::class.java))
			overridePendingTransition(0, 0)
			finish()
		}

		setTheme(
			if (Preferences.get().getSystemDarkMode()) R.style.AppTheme_Dark else R.style.AppTheme
		)

		val binding = ActivityLoginBinding.inflate(layoutInflater)
		setContentView(binding.root)

		supportFragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
			add(R.id.containerFragment, prepareFragment(loginFragment))
			commit()
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		HelperClientUSOS.handleLoginResult(this, intent?.data) {
			startActivity(
				Intent(
					this,
					ActivityMain::class.java
				).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
			)
			finish()
		}
	}

	override fun onBackPressed() {
		if (currentFragment == loginFragment) {
			finish()

			return
		}

		currentFragment = loginFragment
		supportFragmentManager.beginTransaction()
			.replace(R.id.containerFragment, prepareFragment(loginFragment)).commit()
	}

	private fun prepareFragment(fragment: FragmentCoreImpl<*>) = fragment.apply {
		onChangeFragment = { fragment ->
			currentFragment = fragment as FragmentCoreImpl
			supportFragmentManager.beginTransaction()
				.replace(R.id.containerFragment, fragment).commit()
		}
	}
}
