package com.pointlessapps.mobileusos.activities

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.exceptions.ExceptionHttpUnsuccessful
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.managers.FragmentManager
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.repositories.RepositoryEvent
import com.pointlessapps.mobileusos.repositories.RepositoryUser
import com.pointlessapps.mobileusos.services.ServiceUSOSArticle
import com.pointlessapps.mobileusos.services.ServiceUSOSSurvey
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
				(Class.forName(it)?.newInstance() as? FragmentBase)?.also(::changeFragment)
			}
		}

		ensureNotificationSubscription()
	}

	private fun ensureNotificationSubscription() {
		RepositoryEvent().apply {
			RepositoryUser(application).getById(null).onOnceCallback { (user) ->
				if (user != null) {
					ensureNotificationsSubscription(user.id)

					FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
						registerFCMToken(user.id, it.token)
					}

					return@onOnceCallback
				}
			}.onFinished {
				if (it is ExceptionHttpUnsuccessful && it.code == 401) {
					Utils.askForRelogin(this@ActivityMain, R.string.relogin_description)
				}
			}
			ensureEventSubscription()
		}
	}

	private fun RepositoryEvent.ensureNotificationsSubscription(userId: String) {
		GlobalScope.launch(Dispatchers.IO) {
			runCatching {
				val surveys = ServiceUSOSSurvey.init().getToFill()
				val articles = ServiceUSOSArticle.init().getAll()
				Preferences.get().also {
					subscribeNotifications(
						userId,
						it.getAccessToken()?.token ?: return@also,
						it.getAccessToken()?.tokenSecret ?: return@also,
						it.getSelectedUniversity().serviceUrl ?: return@also,
						surveys.map(Survey::id),
						articles.map(Article::id),
					)
				}
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
