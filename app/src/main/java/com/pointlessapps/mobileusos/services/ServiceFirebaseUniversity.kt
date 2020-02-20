package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.University

class ServiceFirebaseUniversity {

	private val database = FirebaseFirestore.getInstance()
	private val collection = database.collection(DATABASE_NAME)

	fun getAll(callback: (List<University>?) -> Unit) {
		collection.get().addOnSuccessListener {
			callback.invoke(it.toObjects(University::class.java).sorted())
		}
	}

	companion object {
		const val DATABASE_NAME = "universities"

		private var INSTANCE: ServiceFirebaseUniversity? = null

		fun getInstance(): ServiceFirebaseUniversity? {
			if (INSTANCE == null) {
				synchronized(AppDatabase::class) {
					INSTANCE = ServiceFirebaseUniversity()
				}
			}
			return INSTANCE
		}
	}
}