package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ServiceFirebaseDatabase private constructor() {

	private val database = FirebaseFirestore.getInstance()
	private val universityCollection = database.collection(UNIVERSITY_COLLECTION)
	private val fcmTokensCollection = database.collection(TOKENS_COLLECTION)

	suspend fun getAllUniversities(): List<University> =
		withContext(Dispatchers.IO) {
			universityCollection.whereEqualTo("available", true).get().await()
				.toObjects(University::class.java)
		}

	suspend fun registerFcmToken(userId: String, fcmToken: String) = withContext(Dispatchers.IO) {
		fcmTokensCollection.document(userId).set(mapOf("token" to fcmToken), SetOptions.merge())
	}

	suspend fun subscribeNotifications(
		userId: String,
		accessToken: String,
		accessTokenSecret: String,
		universityServiceUrl: String,
		surveysIds: List<String>,
		articlesIds: List<String>
	) = withContext(Dispatchers.IO) {
		fcmTokensCollection.document(userId)
			.set(
				mapOf(
					"accessToken" to mapOf(
						"token" to accessToken,
						"secret" to accessTokenSecret,
					),
					"serviceUrl" to universityServiceUrl,
					"surveysIds" to surveysIds,
					"articlesIds" to articlesIds,
				), SetOptions.merge()
			)
	}

	companion object : Utils.SingletonHolder<ServiceFirebaseDatabase, Unit>({
		ServiceFirebaseDatabase()
	}) {
		const val UNIVERSITY_COLLECTION = "universities"
		const val TOKENS_COLLECTION = "fcmTokens"
	}
}
