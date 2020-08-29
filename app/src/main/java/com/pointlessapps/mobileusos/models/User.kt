package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_users")
@Keep
data class User(
	@PrimaryKey
	@SerializedName("id")
	var id: String = UUID.randomUUID().toString(),
	@ColumnInfo(name = "title")
	@SerializedName("title")
	var titles: Title? = null,
	@ColumnInfo(name = "email")
	@SerializedName("email")
	var email: String? = null,
	@ColumnInfo(name = "room")
	@SerializedName("room")
	var room: BuildingRoom? = null,
	@ColumnInfo(name = "office_hours")
	@SerializedName("office_hours")
	var officeHours: Name? = null,
	@ColumnInfo(name = "interests")
	@SerializedName("interests")
	var interests: Name? = null,
	@ColumnInfo(name = "phone_numbers")
	@SerializedName("phone_numbers")
	var phoneNumbers: List<String>? = null,
	@ColumnInfo(name = "employment_functions")
	@SerializedName("employment_functions")
	var employmentFunctions: List<EmploymentFunction>? = null,
	@ColumnInfo(name = "first_name")
	@SerializedName("first_name")
	val firstName: String? = null,
	@ColumnInfo(name = "last_name")
	@SerializedName("last_name")
	val lastName: String? = null,
	@ColumnInfo(name = "student_number")
	@SerializedName("student_number")
	var studentNumber: String? = null,
	@ColumnInfo(name = "photo_urls")
	@SerializedName("photo_urls")
	var photoUrls: Map<String, String>? = null,
	@ColumnInfo(name = "logged_in")
	@SerializedName("logged_in")
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

	@Keep
	class Title(
		@SerializedName("before")
		var before: String?,
		@SerializedName("after")
		var after: String?
	)

	@Keep
	class EmploymentFunction(
		@SerializedName("function")
		var function: Name?,
		@SerializedName("faculty")
		var faculty: Faculty
	)

	@Keep
	class Faculty(
		@SerializedName("id")
		var id: String,
		@SerializedName("name")
		var name: Name?
	) {
		companion object {
			const val BASE_FACULTY_ID = "0000000"
		}
	}
}
