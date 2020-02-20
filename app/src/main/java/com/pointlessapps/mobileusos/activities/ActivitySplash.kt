package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ActivitySplash : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

//		TODO: add firebase
//		FirebaseApp.initializeApp(applicationContext)

		startActivity(Intent(this, ActivityMain::class.java))
		finish()
	}
}