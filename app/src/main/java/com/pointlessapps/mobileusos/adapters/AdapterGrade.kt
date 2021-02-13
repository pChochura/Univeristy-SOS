package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.databinding.ListItemGradeChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemGradeHeaderBinding
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Term

class AdapterGrade(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterGrade.SectionHeader, Grade, AdapterGrade.SectionViewHolder, AdapterGrade.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Grade) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemGradeHeaderBinding.inflate(
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
		itemView.binding.termName.text =
			(section.getSectionHeader().name ?: section.getSectionHeader().id).toString()

		val grades = section.childItems.filter { it.countsIntoAverage == "T" }
		itemView.binding.termAverage.text = "%.2f".format(grades.sumByDouble { grade ->
			grade.valueSymbol?.replace(Regex("(\\d),(\\d).*")) {
				"${it.groups[1]?.value}.${it.groups[2]?.value}"
			}?.toDoubleOrNull() ?: 2.0
		} / grades.count())

		(itemView.binding.termAverage.parent as View).isGone = grades.count() == 0
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemGradeChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		grade: Grade
	) {
		itemView.binding.root.setOnClickListener {
			onClickListener(grade)
		}

		itemView.binding.gradeName.text = grade.courseName?.toString()
		itemView.binding.gradeValue.text = grade.valueSymbol
		if (grade.valueSymbol.isNullOrBlank()) {
			itemView.binding.gradeValue.isGone = true
		}
	}

	class SectionHeader(private val term: Term, private val grades: List<Grade>) : Section<Grade> {
		override fun getChildItems() = grades
		fun getSectionHeader() = term
	}

	class SectionViewHolder(val binding: ListItemGradeHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemGradeChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
