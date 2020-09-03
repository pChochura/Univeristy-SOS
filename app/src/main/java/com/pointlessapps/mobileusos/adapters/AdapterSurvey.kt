package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.Utils
import org.jetbrains.anko.find

class AdapterSurvey(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterSurvey.SectionHeader, Survey, AdapterSurvey.SectionViewHolder, AdapterSurvey.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Survey) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_survey_header, itemView, false)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.textHeader.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_survey_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		survey: Survey
	) {
		if (survey.didIFillOut == false) {
			itemView.bg.setOnClickListener {
				onClickListener(survey)
			}
		} else {
			itemView.bg.isClickable = false
			itemView.bg.isFocusable = false
		}

		if (survey.surveyType == Survey.SurveyType.Course) {
			itemView.textLecturer.text = survey.lecturer?.name()
			itemView.textName.text = context.getString(
				R.string.course_info_general,
				survey.group?.courseName?.toString(),
				survey.group?.classType?.toString()
			)
			itemView.textName.isGone = false
		} else {
			itemView.textLecturer.text = Utils.stripHtmlTags(survey.name.toString())
			itemView.textName.isGone = true
		}
		itemView.textDate.text = context.getString(R.string.date).format(survey.endDate)
	}

	class SectionHeader(private val header: String, private val surveys: List<Survey>) :
		Section<Survey> {
		override fun getChildItems() = surveys
		fun getSectionHeader() = header
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textHeader = itemView.find<AppCompatTextView>(R.id.surveyHeader)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.surveyName)
		val textLecturer = itemView.find<AppCompatTextView>(R.id.surveyLecturer)
		val textDate = itemView.find<Chip>(R.id.surveyDate)
	}
}
