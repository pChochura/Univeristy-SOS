package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ServiceFirebaseUniversity private constructor() {

	private val database = FirebaseFirestore.getInstance()
	private val collection = database.collection(DATABASE_NAME)

	suspend fun getAll(): List<University> =
		withContext(Dispatchers.IO) {
			collection.get().await().toObjects(University::class.java)
		}

	companion object : Utils.SingletonHolder<ServiceFirebaseUniversity, Unit>({
		ServiceFirebaseUniversity()
	}) {
		const val DATABASE_NAME = "universities"
	}
}
