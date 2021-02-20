package com.pointlessapps.mobileusos.activities

import android.animation.LayoutTransition
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ActivityMainBinding
import com.pointlessapps.mobileusos.databinding.DialogMessageBinding
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
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ActivityMain : FragmentActivity() {

	private lateinit var fragmentManager: FragmentManager

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setTheme(
			if (Preferences.get().getSystemDarkMode()) R.style.AppTheme_Dark else R.style.AppTheme
		)
		FirebaseApp.initializeApp(applicationContext)
		Preferences.init(applicationContext)

		val binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		binding.root.layoutTransition.enableTransitionType(LayoutTransition.CHANGE_APPEARING)
		binding.root.setBackgroundColor(themeColor(R.attr.colorBackground))

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
				(Class.forName(it)?.newInstance() as? FragmentCoreImpl<*>)?.also(::changeFragment)
			}
		}

		ensureNotificationSubscription()
		checkRating()
	}

	private fun checkRating() {
		if (Preferences.get().getSystemLaunchCount() > 0 &&
			Preferences.get().getSystemLaunchCount() % 10 == 0 &&
			!Preferences.get().getSystemReviewDiscarded()
		) {
			val reviewManager = ReviewManagerFactory.create(applicationContext)
			reviewManager.requestReviewFlow().addOnCompleteListener { reviewInfo ->
				if (!reviewInfo.isSuccessful) {
					return@addOnCompleteListener
				}

				DialogUtil.create(this, DialogMessageBinding::class.java, { dialog ->
					dialog.messageMain.setText(R.string.review_this_app)
					dialog.messageSecondary.setText(R.string.review_this_app_description)
					dialog.buttonPrimary.setText(android.R.string.ok)
					dialog.buttonSecondary.setText(R.string.later)
					dialog.buttonTertiary.setText(R.string.no)
					dialog.buttonTertiary.isVisible = true

					dialog.buttonPrimary.setOnClickListener {
						dismiss()
						reviewManager.launchReviewFlow(this@ActivityMain, reviewInfo.result)
					}
					dialog.buttonSecondary.setOnClickListener { dismiss() }
					dialog.buttonTertiary.setOnClickListener {
						Preferences.get().putSystemReviewDiscarded(true)
						dismiss()
					}
				})
			}
		}
	}

	private fun ensureNotificationSubscription() {
		if (!Preferences.get().getScopeEvents()) {
			return
		}

		RepositoryEvent().apply {
			RepositoryUser(application).getById(null).onOnceCallback { (user) ->
				if (user != null) {
					ensureNotificationsSubscription(user.id)

					FirebaseMessaging.getInstance().token.addOnSuccessListener {
						registerFCMToken(user.id, it)
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
		super.attachBaseContext(LocaleHelper.withDefaultLocale(newBase))
	}

	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		LocaleHelper.withDefaultLocale(this)
	}

	override fun onBackPressed() {
		if (!fragmentManager.popHistory()) {
			super.onBackPressed()
		}
	}
}
