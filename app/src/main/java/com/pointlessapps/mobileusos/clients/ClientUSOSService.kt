package com.pointlessapps.mobileusos.clients

import android.net.Uri
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.services.USOSApi
import com.pointlessapps.mobileusos.utils.Utils
import java.util.*

class ClientUSOSService private constructor() : USOSApi() {

	companion object : Utils.SingletonHolder<ClientUSOSService, Unit>({ ClientUSOSService() })

	fun userDetailsRequest(userId: String?) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/users/user").buildUpon().apply {
			if (userId != null) {
				appendQueryParameter("user_id", userId)
			}
			appendQueryParameter(
				"fields",
				"id|titles|email|first_name|last_name|student_number|photo_urls[200x200]"
			)
		}.build().toString()
	)

	fun userGroupRequest(courseUnitId: String, groupNumber: Int) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/groups/group").buildUpon()
			.appendQueryParameter(
				"fields",
				"course_unit_id|class_type_id|term_id|course_id|class_type|course_name|group_number|lecturers|participants"
			)
			.appendQueryParameter("course_unit_id", courseUnitId)
			.appendQueryParameter("group_number", groupNumber.toString())
			.build().toString()
	)

	fun userGroupsRequest() = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/groups/user").buildUpon()
			.appendQueryParameter(
				"fields",
				"course_unit_id|class_type_id|term_id|course_id|class_type|course_name|group_number|lecturers"
			)
			.appendQueryParameter("active_terms", "true")
			.build().toString()
	)

	fun termsRequest(termIds: List<String>) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/terms/terms").buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|order_key|start_date|end_date"
			)
			.appendQueryParameter("term_ids", termIds.joinToString("|"))
			.build().toString()
	)

	fun termsRequest() = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/terms/search").buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|order_key|start_date|end_date"
			)
			.build().toString()
	)

	fun userGradesRequest(course: Course) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/grades/course_edition").buildUpon()
			.appendQueryParameter(
				"fields",
				"value_symbol|value_description|date_modified|counts_into_average|comment"
			)
			.appendQueryParameter("course_id", course.courseId)
			.appendQueryParameter("term_id", course.termId)
			.build().toString()
	)

	fun userGradesRequest(termIds: List<String>) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/grades/terms2").buildUpon()
			.appendQueryParameter(
				"fields",
				"value_symbol|value_description|date_modified|counts_into_average|comment|exam_id|exam_session_number|modification_author"
			)
			.appendQueryParameter("term_ids", termIds.joinToString("|"))
			.build().toString()
	)

	fun examReportRequest(examId: String) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/examrep/exam").buildUpon()
			.appendQueryParameter(
				"fields",
				"grades_distribution|description|id"
			)
			.appendQueryParameter("id", examId)
			.build().toString()
	)

	fun timetableRequest(id: String? = null, startDate: Calendar, days: Int = 7) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}${if (id == null) "/tt/user" else "/tt/staff"}")
			.buildUpon().apply {
				id?.also { appendQueryParameter("user_id", id) }
				appendQueryParameter("start", dateFormat.format(startDate.time))
				appendQueryParameter("days", days.toString())
				appendQueryParameter(
					"fields",
					"start_time|end_time|name|course_id|course_name|building_name|building_id|room_number|group_number|room_id|frequency|classtype_id|unit_id|classtype_name|lecturer_ids"
				)
			}.build().toString()
	)

	fun timetableRequest(unitId: String, groupNumber: Int) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/tt/classgroup")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"start_time|end_time|name|course_name|building_name|room_number|room_id|classtype_id"
			)
			.appendQueryParameter("unit_id", unitId)
			.appendQueryParameter("group_number", groupNumber.toString())
			.build().toString()
	)

	fun timetableRequest(roomId: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/tt/room")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"start_time|end_time|name|course_name|building_name|room_number|room_id|classtype_name|group_number"
			)
			.appendQueryParameter("room_id", roomId)
			.build().toString()
	)

	fun newsRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/news/search")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"items[article[id|author|publication_date|headline_html|content_html|event|category]]"
			)
			.appendQueryParameter("num", "20")
			.build().toString()
	)

	fun newsCategoriesRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/news/category_index")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name"
			)
			.build().toString()
	)

	fun roomRequest(roomId: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/geo/room")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|number|building[id|name|static_map_urls[600x300]|location]|capacity|attributes"
			)
			.appendQueryParameter("room_id", roomId)
			.build().toString()
	)
}
