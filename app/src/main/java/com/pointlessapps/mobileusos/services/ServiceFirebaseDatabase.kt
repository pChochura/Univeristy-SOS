package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
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
			universityCollection.get().await().toObjects(University::class.java)
		}

	suspend fun registerFcmToken(userId: String, fcmToken: String): Unit =
		withContext(Dispatchers.Main) {
			fcmTokensCollection.document(userId).set(mapOf("token" to fcmToken))
		}

	companion object : Utils.SingletonHolder<ServiceFirebaseDatabase, Unit>({
		ServiceFirebaseDatabase()
	}) {
		const val UNIVERSITY_COLLECTION = "universities"
		const val TOKENS_COLLECTION = "fcmTokens"
	}
}
