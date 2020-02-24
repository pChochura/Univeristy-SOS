package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Utils

class ServiceFirebaseUniversity private constructor() {

	private val database = FirebaseFirestore.getInstance()
	private val collection = database.collection(DATABASE_NAME)

	fun getAll(callback: (List<University>?) -> Unit) {
		collection.get().addOnSuccessListener {
			callback.invoke(it.toObjects(University::class.java).sorted())
		}
	}

	companion object : Utils.SingletonHolder<ServiceFirebaseUniversity, Unit>({
		ServiceFirebaseUniversity()
	}) {
		const val DATABASE_NAME = "universities"
	}
}