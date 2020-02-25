package com.pointlessapps.mobileusos.services

import com.google.firebase.firestore.FirebaseFirestore
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.utils.Callback
import com.pointlessapps.mobileusos.utils.Utils

class ServiceFirebaseUniversity private constructor() {

	private val database = FirebaseFirestore.getInstance()
	private val collection = database.collection(DATABASE_NAME)

	fun getAll(): Callback<List<University>?> {
		val callback = Callback<List<University>?>()
		collection.get().addOnSuccessListener {
			callback.post(it.toObjects(University::class.java))
		}
		return callback
	}

	companion object : Utils.SingletonHolder<ServiceFirebaseUniversity, Unit>({
		ServiceFirebaseUniversity()
	}) {
		const val DATABASE_NAME = "universities"
	}
}