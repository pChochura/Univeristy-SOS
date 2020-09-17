package com.pointlessapps.mobileusos.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityMain
import com.pointlessapps.mobileusos.fragments.FragmentGrades
import com.pointlessapps.mobileusos.fragments.FragmentNews
import com.pointlessapps.mobileusos.fragments.FragmentProfile
import com.pointlessapps.mobileusos.fragments.FragmentSurveys
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.repositories.RepositoryEvent
import com.pointlessapps.mobileusos.repositories.RepositoryUser

class ServiceFirebaseMessaging : FirebaseMessagingService() {

	override fun onMessageReceived(message: RemoteMessage) {
		val prefs = Preferences.init(applicationContext).get()

		if (!prefs.getNotificationsEnabled()) {
			return
		}

		if (message.data.isNotEmpty()) {
			when {
				message.data["eventType"] in listOf(
					"grades/grade",
					"crstests/user_grade"
				) && prefs.getNotificationsGrades() -> when (message.data["operation"]) {
					"create" -> {
						sendNotification(
							getString(R.string.you_have_new_grade),
							FragmentProfile::class.java.name,
							R.string.grade_notification_channel,
							R.string.grade_notification_channel_id
						)
					}
					"update" -> {
						sendNotification(
							getString(R.string.grade_has_been_updated),
							FragmentGrades::class.java.name,
							R.string.default_notification_channel,
							R.string.grade_notification_channel_id
						)
					}
				}
				message.data["eventType"] == "surveys/surveys" && prefs.getNotificationsSurveys() -> sendNotification(
					getString(R.string.survey_has_appeared),
					FragmentSurveys::class.java.name,
					R.string.survey_notification_channel,
					R.string.survey_notification_channel_id
				)
				message.data["eventType"] == "news/articles" && prefs.getNotificationsNews() -> sendNotification(
					getString(R.string.article_has_appeared),
					FragmentNews::class.java.name,
					R.string.article_notification_channel,
					R.string.article_notification_channel_id
				)
			}
		}
	}

	override fun onNewToken(token: String) {
		Preferences.init(applicationContext)
		if (Preferences.get().getAccessToken() == null) {
			return
		}

		RepositoryUser(application).getById(null).onOnceCallback { (user) ->
			if (user != null) {
				RepositoryEvent().registerFCMToken(user.id, token)

				return@onOnceCallback
			}
		}
	}

	private fun sendNotification(
		messageBody: String,
		destinationFragmentName: String? = null,
		@StringRes channelName: Int = R.string.default_notification_channel,
		@StringRes channelId: Int = R.string.default_notification_channel_id
	) {
		val pendingIntent =
			PendingIntent.getActivity(this, 0, Intent(this, ActivityMain::class.java).apply {
				addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
				putExtra("destinationFragmentName", destinationFragmentName)
			}, PendingIntent.FLAG_ONE_SHOT)

		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val notificationBuilder = NotificationCompat.Builder(this, getString(channelId))
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(getString(R.string.usos_notification_title))
			.setContentText(messageBody)
			.setAutoCancel(true)
			.setSound(defaultSoundUri)
			.setContentIntent(pendingIntent)

		val notificationManager =
			getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				getString(channelId),
				getString(channelName),
				NotificationManager.IMPORTANCE_DEFAULT
			)
			notificationManager.createNotificationChannel(channel)
		}

		notificationManager.notify(0, notificationBuilder.build())
	}
}
