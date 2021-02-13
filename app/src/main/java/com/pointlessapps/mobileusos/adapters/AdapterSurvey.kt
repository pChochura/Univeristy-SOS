package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemSurveyChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemSurveyHeaderBinding
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.Utils

class AdapterSurvey(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterSurvey.SectionHeader, Survey, AdapterSurvey.SectionViewHolder, AdapterSurvey.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Survey) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemSurveyHeaderBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.binding.surveyHeader.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemSurveyChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		survey: Survey
	) {
		if (survey.didIFillOut == false) {
			itemView.binding.root.setOnClickListener {
				onClickListener(survey)
			}
			itemView.binding.surveyDate.isVisible = true
		} else {
			itemView.binding.root.isClickable = false
			itemView.binding.root.isFocusable = false
			itemView.binding.surveyDate.isVisible = false
		}

		if (survey.surveyType == Survey.SurveyType.Course) {
			itemView.binding.surveyLecturer.text = survey.lecturer?.name()
			itemView.binding.surveyName.text = context.getString(
				R.string.course_info_general,
				survey.group?.courseName?.toString(),
				survey.group?.classType?.toString()
			)
			itemView.binding.surveyName.isGone = false
		} else {
			itemView.binding.surveyLecturer.text = Utils.stripHtmlTags(survey.name.toString())
			itemView.binding.surveyName.isGone = true
		}
		itemView.binding.surveyDate.text = context.getString(R.string.date).format(survey.endDate)
	}

	class SectionHeader(private val header: String, private val surveys: List<Survey>) :
		Section<Survey> {
		override fun getChildItems() = surveys
		fun getSectionHeader() = header
	}

	class SectionViewHolder(val binding: ListItemSurveyHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemSurveyChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
