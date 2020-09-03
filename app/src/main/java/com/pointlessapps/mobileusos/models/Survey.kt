package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "table_surveys")
@Keep
data class Survey(
	@ColumnInfo(name = "number_of_answers")
	@SerializedName("number_of_answers")
	var numberOfAnswers: Int = 0,
	@ColumnInfo(name = "questions")
	@SerializedName("questions")
	var questions: List<Question>? = null,
	@ColumnInfo(name = "lecturer")
	@SerializedName("lecturer")
	var lecturer: User? = null,
	@ColumnInfo(name = "group")
	@SerializedName("group")
	var group: Course? = null,
	@ColumnInfo(name = "did_i_fill_out")
	@SerializedName("did_i_fill_out")
	var didIFillOut: Boolean? = null,
	@ColumnInfo(name = "can_i_fill_out")
	@SerializedName("can_i_fill_out")
	var canIFillOut: Boolean? = null,
	@ColumnInfo(name = "end_date")
	@SerializedName("end_date")
	var endDate: Date? = null,
	@ColumnInfo(name = "start_date")
	@SerializedName("start_date")
	var startDate: Date? = null,
	@ColumnInfo(name = "headline_html")
	@SerializedName("headline_html")
	var headlineHtml: String? = null,
	@ColumnInfo(name = "name")
	@SerializedName("name")
	var name: Name? = null,
	@ColumnInfo(name = "survey_type")
	@SerializedName("survey_type")
	var surveyType: String? = null,
	@PrimaryKey
	@SerializedName("id")
	var id: String = ""
) {
	object SurveyType {
		const val Course = "course"
		const val General = "general"
	}

	@Keep
	class Question(
		@SerializedName("sub_questions")
		var subQuestions: List<Question>?,
		@SerializedName("possible_answers")
		var possibleAnswers: List<Answer>?,
		@SerializedName("display_text_html")
		var displayTextHtml: Name,
		@SerializedName("level")
		var level: Int,
		@SerializedName("number")
		var number: String,
		@SerializedName("id")
		var id: String
	) {

		@Keep
		class Answer(
			@SerializedName("display_text_html")
			var displayTextHtml: Name,
			@SerializedName("id")
			var id: String
		)
	}
}
