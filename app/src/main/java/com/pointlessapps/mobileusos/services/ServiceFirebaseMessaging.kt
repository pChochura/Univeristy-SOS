package com.pointlessapps.mobileusos.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivitySplash
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.repositories.RepositoryEvent

class ServiceFirebaseMessaging : FirebaseMessagingService() {

	override fun onMessageReceived(message: RemoteMessage) {
		Preferences.init(applicationContext)
		val prefs = Preferences.get()

		// TODO: remove it after testing
		message.data.takeIf(Map<String, String>::isNotEmpty)?.also {
			val size = prefs.getInt("notificationSize", 0)
			prefs.put("notificationSize", size + 1)
			prefs.put("notification_$size", Gson().toJson(it))
		}

		if (!prefs.getNotificationsEnabled()) {
			return
		}

		if (message.data.isNotEmpty()) {
			when {
				message.data["event_type"] == "grades/grade" && prefs.getNotificationsGrades() -> Unit
				message.data["event_type"] == "surveys/surveys" && prefs.getNotificationsSurveys() -> Unit
				message.data["event_type"] == "news/articles" && prefs.getNotificationsNews() -> Unit
			}
			sendNotification(message.data.toString())
		}
	}

	override fun onNewToken(token: String) {
		Preferences.init(applicationContext)
		if (Preferences.get().getAccessToken() == null) {
			return
		}

		RepositoryEvent().registerFCMToken(token)
	}

	private fun sendNotification(messageBody: String) {
		val intent = Intent(this, ActivitySplash::class.java)
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
		val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

		val channelId = getString(R.string.default_notification_channel_id)
		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		val notificationBuilder = NotificationCompat.Builder(this, channelId)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle("FCM notification")
			.setContentText(messageBody)
			.setAutoCancel(true)
			.setSound(defaultSoundUri)
			.setContentIntent(pendingIntent)

		val notificationManager =
			getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				channelId,
				getString(R.string.default_notification_channel),
				NotificationManager.IMPORTANCE_DEFAULT
			)
			notificationManager.createNotificationChannel(channel)
		}

		notificationManager.notify(0, notificationBuilder.build())
	}
}
