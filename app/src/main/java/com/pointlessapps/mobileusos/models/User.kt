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
	var titles: Title? = null,
	@ColumnInfo(name = "email")
	var email: String? = null,
	@ColumnInfo(name = "room")
	var room: BuildingRoom? = null,
	@ColumnInfo(name = "office_hours")
	var officeHours: Name? = null,
	@ColumnInfo(name = "interests")
	var interests: Name? = null,
	@ColumnInfo(name = "phone_numbers")
	var phoneNumbers: List<String>? = null,
	@ColumnInfo(name = "employment_functions")
	var employmentFunctions: List<EmploymentFunction>? = null,
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
		titles?.before?.also {
			name = "$it $name"
		}
		titles?.after?.also {
			name = "$name $it"
		}
		return name
	}

	class Title(var before: String?, var after: String?)

	class EmploymentFunction(
		var function: Name?,
		var faculty: Faculty,
		var isOfficial: Boolean = false
	)

	class Faculty(var id: String, var name: Name?) {
		companion object {
			const val BASE_FACULTY_ID = "0000000"
		}
	}
}
