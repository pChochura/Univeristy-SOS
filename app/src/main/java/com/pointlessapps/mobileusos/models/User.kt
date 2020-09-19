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
	@ColumnInfo(name = "titles")
	@SerializedName("titles")
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

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as User

		if (id != other.id) return false
		if (titles != other.titles) return false
		if (email != other.email) return false
		if (firstName != other.firstName) return false
		if (lastName != other.lastName) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + (titles?.hashCode() ?: 0)
		result = 31 * result + (email?.hashCode() ?: 0)
		result = 31 * result + (firstName?.hashCode() ?: 0)
		result = 31 * result + (lastName?.hashCode() ?: 0)
		return result
	}

	fun name(withTitles: Boolean = true): String {
		var name = "$firstName $lastName"
		if (!withTitles) {
			return name
		}

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
	) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (javaClass != other?.javaClass) return false

			other as Title

			if (before != other.before) return false
			if (after != other.after) return false

			return true
		}

		override fun hashCode(): Int {
			var result = before?.hashCode() ?: 0
			result = 31 * result + (after?.hashCode() ?: 0)
			return result
		}
	}

	@Keep
	class EmploymentFunction(
		@SerializedName("function")
		var function: Name?,
		@SerializedName("faculty")
		var faculty: Faculty
	)

	@Keep
	@Entity(tableName = "table_faculties")
	class Faculty(
		@PrimaryKey
		@SerializedName("id")
		var id: String,
		@ColumnInfo(name = "name")
		@SerializedName("name")
		var name: Name?
	)
}
