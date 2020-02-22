package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.helpers.Preferences

class ActivitySplash : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		FirebaseApp.initializeApp(applicationContext)
		Preferences.init(applicationContext)

		startActivity(
			Intent(
				this,
				if (HelperClientUSOS.isLoggedIn()) {
					ActivityMain::class.java
				} else {
					ActivityLogin::class.java
				}
			)
		)
		finish()
	}
}