package com.pointlessapps.mobileusos.clients

import android.net.Uri
import com.github.scribejava.core.httpclient.multipart.MultipartPayload
import com.github.scribejava.core.model.OAuthRequest
import com.github.scribejava.core.model.Verb
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
				"id|titles|email|first_name|last_name|student_number|photo_urls[200x200]|room|employment_functions|interests|office_hours|phone_numbers"
			)
		}.build().toString()
	)

	fun usersRequest(userIds: List<String>) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/users/users").buildUpon()
			.appendQueryParameter(
				"fields",
				"id|first_name|last_name|email|titles|photo_urls[200x200]"
			)
			.appendQueryParameter("user_ids", userIds.joinToString("|"))
			.build().toString()
	)

	fun usersRequest(query: String, startIndex: Int = 0) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/users/search2").buildUpon()
			.appendQueryParameter(
				"fields",
				"items[user[id|first_name|last_name|email|titles]]|next_page"
			)
			.appendQueryParameter("lang", "pl")
			.appendQueryParameter("query", query)
			.appendQueryParameter("num", "20")
			.appendQueryParameter("start", startIndex.toString())
			.build().toString()
	)

	fun userGroupRequest(courseUnitId: String, groupNumber: Int) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/groups/group").buildUpon()
			.appendQueryParameter(
				"fields",
				"course_unit_id|class_type_id|term_id|course_id|class_type|course_name|group_number|lecturers|participants|course_learning_outcomes|course_description|course_assessment_criteria"
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

	fun userGradesRequest(termIds: List<String>) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/grades/terms2").buildUpon()
			.appendQueryParameter(
				"fields",
				"value_symbol|value_description|date_modified|counts_into_average|comment|exam_id|exam_session_number|modification_author"
			)
			.appendQueryParameter("term_ids", termIds.joinToString("|"))
			.build().toString()
	)

	fun userRecentGradesRequest() = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/grades/latest").buildUpon()
			.appendQueryParameter(
				"fields",
				"value_symbol|value_description|date_modified|counts_into_average|comment|exam_id|exam_session_number|modification_author"
			)
			.appendQueryParameter("days", "7")
			.build().toString()
	)

	fun userGradeByExamRequest(examId: String, examSessionNumber: Int) = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/grades/grade").buildUpon()
			.appendQueryParameter(
				"fields",
				"value_symbol|value_description|date_modified|counts_into_average|comment|exam_id|exam_session_number|modification_author|course_edition[course_name]"
			)
			.appendQueryParameter("exam_id", examId)
			.appendQueryParameter("exam_session_number", examSessionNumber.toString())
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

	fun testsRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/crstests/participant2").buildUpon()
			.appendQueryParameter(
				"fields",
				"course_edition|is_limited_to_groups|class_groups|root[id|name|description|visible_for_students|type|subnodes|students_points]"
			)
			.appendQueryParameter("active_terms_only", "false")
			.build().toString()
	)

	fun testNodeByIdRequest(id: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/crstests/node2").buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|description|visible_for_students|type|subnodes|students_points|task_node_details|folder_node_details|grade_node_details[students_grade]"
			)
			.appendQueryParameter("node_id", id)
			.build().toString()
	)

	fun testGradesByNodeIdsRequest(ids: List<String>) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/crstests/user_grades").buildUpon()
			.appendQueryParameter(
				"fields",
				"node_id|grade|grader_id|last_changed|public_comment"
			)
			.appendQueryParameter("node_ids", ids.joinToString("|"))
			.build().toString()
	)

	fun testPointsByNodeIdsRequest(ids: List<String>) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/crstests/user_points").buildUpon()
			.appendQueryParameter(
				"fields",
				"node_id|points|grader_id|last_changed|public_comment"
			)
			.appendQueryParameter("node_ids", ids.joinToString("|"))
			.build().toString()
	)

	fun timetableRequest(startDate: Calendar, days: Int = 7) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/tt/user")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"start_time|end_time|name|course_id|course_name|building_name|building_id|room_number|group_number|room_id|frequency|classtype_id|unit_id|classtype_name|lecturer_ids"
			)
			.appendQueryParameter("days", days.toString())
			.appendQueryParameter("start", dateFormat.format(startDate.time))
			.build().toString()
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
				"id|number|building[id|name|static_map_urls[600x300]|location|campus_name]|capacity|attributes"
			)
			.appendQueryParameter("room_id", roomId)
			.build().toString()
	)

	fun buildingRequest(building: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/geo/building2")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|static_map_urls[600x300]|location|campus_name|rooms[id|number|capacity]|phone_numbers|all_phone_numbers"
			)
			.appendQueryParameter("building_id", building)
			.appendQueryParameter("langpref", "pl")
			.build().toString()
	)

	fun emailsRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/user")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|subject|status|date"
			)
			.build().toString()
	)

	fun emailRequest(emailId: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/message")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|subject|status|date|content|attachments[id|filename|size|description|url]"
			)
			.appendQueryParameter("message_id", emailId)
			.build().toString()
	)

	fun surveysToFillRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/surveys/surveys_to_fill")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|survey_type|end_date|can_i_fill_out|did_i_fill_out|lecturer[id|first_name|last_name|titles]|group[class_type_id|term_id|course_id|course_name|class_type]"
			)
			.appendQueryParameter("include_filled_out", "true")
			.build().toString()
	)

	fun surveyRequest(surveyId: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/surveys/survey")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|survey_type|end_date|can_i_fill_out|did_i_fill_out|lecturer[id|first_name|last_name|titles|photo_urls[200x200]]|group[class_type_id|term_id|course_id|course_name|class_type]|questions"
			)
			.appendQueryParameter("survey_id", surveyId)
			.build().toString()
	)

	fun fillOutSurveyRequest(surveyId: String, answers: Map<String, String>, comment: String?) =
		OAuthRequest(
			Verb.GET,
			Uri.parse("${selectedUniversity?.serviceUrl}/surveys/fill_out")
				.buildUpon()
				.appendQueryParameter("survey_id", surveyId)
				.appendQueryParameter("answers", gson.toJson(answers))
				.appendQueryParameter("comment", comment ?: "")
				.build().toString()
		)

	fun emailRecipientsRequest(emailId: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/recipients")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"email|user[first_name|last_name|titles|email|photo_urls[200x200]]"
			)
			.appendQueryParameter("message_id", emailId)
			.build().toString()
	)

	fun deleteEmailRequest(id: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/delete_messages")
			.buildUpon()
			.appendQueryParameter("message_ids", id)
			.build().toString()
	)

	fun createEmailRequest(subject: String, content: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/create_message")
			.buildUpon()
			.appendQueryParameter("subject", subject)
			.appendQueryParameter("content", content)
			.appendQueryParameter("attachments_lifetime", "90")
			.build().toString()
	)

	fun updateEmailRequest(id: String, subject: String, content: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/update_message")
			.buildUpon()
			.appendQueryParameter("message_id", id)
			.appendQueryParameter("subject", subject)
			.appendQueryParameter("content", content)
			.appendQueryParameter("attachments_lifetime", "90")
			.build().toString()
	)

	fun sendEmailRequest(id: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/send_message")
			.buildUpon()
			.appendQueryParameter("message_id", id)
			.appendQueryParameter("lang", Locale.getDefault().toLanguageTag())
			.build().toString()
	)

	fun updateEmailRecipientsRequest(id: String, userIds: List<String>, emails: List<String>) =
		OAuthRequest(
			Verb.GET,
			Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/update_recipients_group")
				.buildUpon()
				.appendQueryParameter("message_id", id)
				.appendQueryParameter("strict", "false")
				.apply {
					if (userIds.isNotEmpty()) {
						appendQueryParameter("user_ids", userIds.joinToString("|"))
					}
					if (emails.isNotEmpty()) {
						appendQueryParameter("emails", emails.joinToString("|"))
					}
				}
				.build().toString()
		)

	fun refreshEmailRecipientsRequest(id: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/refresh_recipients")
			.buildUpon()
			.appendQueryParameter("message_id", id)
			.appendQueryParameter("email_source", "primary_preferred")
			.build().toString()
	)

	fun addEmailAttachmentRequest(id: String, data: ByteArray, filename: String) =
		OAuthRequest(
			Verb.POST,
			Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/put_attachment")
				.buildUpon()
				.appendQueryParameter("message_id", id)
				.appendQueryParameter("filename", filename)
				.build().toString()
		).apply {
			val boundary = "---attachment"
			addHeader("Content-Type", "multipart/form-data; boundary=$boundary")
			multipartPayload = MultipartPayload(boundary).apply {
				addFileBodyPart("application/octet-stream", data, "data", filename)
			}
		}

	fun deleteEmailAttachmentRequest(id: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/mailclient/delete_attachments")
			.buildUpon()
			.appendQueryParameter("attachment_ids", id)
			.build().toString()
	)

	fun calendarRequest(faculty: String, startDate: Date, endDate: Date) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/calendar/search")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|name|start_date|end_date|type|is_day_off|faculty[id]"
			)
			.appendQueryParameter("faculty_id", faculty)
			.appendQueryParameter("start_date", dateFormat.format(startDate))
			.appendQueryParameter("end_date", dateFormat.format(endDate))
			.build().toString()
	)

	fun subscribeEventRequest(eventType: String) = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/events/subscribe_event")
			.buildUpon()
			.appendQueryParameter("event_type", eventType)
			.appendQueryParameter(
				"callback_url",
				"https://us-central1-usoschedule-221315.cloudfunctions.net/postNotification"
			)
			.build().toString()
	)

	fun subscriptionsRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/events/subscriptions")
			.buildUpon()
			.build().toString()
	)

	fun primaryFacultyRequest() = OAuthRequest(
		Verb.GET,
		Uri.parse("${selectedUniversity?.serviceUrl}/apisrv/installation")
			.buildUpon()
			.appendQueryParameter("fields", "institution")
			.build().toString()
	)

	fun guideRequest() = OAuthRequest(
		Verb.GET, Uri.parse("${selectedUniversity?.serviceUrl}/guide/guide")
			.buildUpon()
			.appendQueryParameter(
				"fields",
				"id|title|pages[id|title|entries[id|title|content|image_urls[720x405]]]"
			)
			.appendQueryParameter("lang", Locale.getDefault().toLanguageTag())
			.build().toString()
	)
}
