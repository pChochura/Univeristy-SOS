package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class ActivitySplash : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		FirebaseApp.initializeApp(applicationContext)

		startActivity(Intent(this, ActivityLogin::class.java))
		finish()
	}
}