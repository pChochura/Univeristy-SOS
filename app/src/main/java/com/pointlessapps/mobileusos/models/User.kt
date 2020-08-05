package com.pointlessapps.mobileusos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_users")
data class User(
	@PrimaryKey
	var id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "title")
	var title: Title? = null,
	@ColumnInfo(name = "email")
	var email: String? = null,
	@ColumnInfo(name = "first_name")
	val firstName: String? = null,
	@ColumnInfo(name = "last_name")
	val lastName: String? = null,
	@ColumnInfo(name = "student_number")
	var studentNumber: String? = null,
	@ColumnInfo(name = "photo_urls")
	var photoUrls: Map<String, String>? = null,
	@ColumnInfo(name = "logged_in")
	var loggedIn: Boolean = false
) : Comparable<User> {

	override fun compareTo(other: User) =
		compareValuesBy(this, other, { it.lastName })

	fun name(): String {
		var name = "$firstName $lastName"
		title?.before?.also {
			name = "$it $name"
		}
		title?.after?.also {
			name = "$name $it"
		}
		return name
	}

	class Title {
		var before: String? = null
		var after: String? = null
	}
}
