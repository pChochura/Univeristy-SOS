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
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Term
import org.jetbrains.anko.find

class AdapterGrade(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterGrade.SectionHeader, Grade, AdapterGrade.SectionViewHolder, AdapterGrade.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Grade) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_grade_header, itemView, false)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.textName.text =
			(section.getSectionHeader().name ?: section.getSectionHeader().id).toString()

		val grades = section.childItems.filter { it.countsIntoAverage == "T" }
		itemView.textAverage.text = "%.2f".format(grades.sumByDouble { grade ->
			grade.valueSymbol?.replace(Regex("(\\d),(\\d).*")) {
				"${it.groups[1]?.value}.${it.groups[2]?.value}"
			}?.toDoubleOrNull() ?: 2.0
		} / grades.count())

		(itemView.textAverage.parent as View).isGone = grades.count() == 0
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_grade_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		grade: Grade
	) {
		itemView.bg.setOnClickListener {
			onClickListener(grade)
		}

		itemView.textName.text = grade.courseName?.toString()
		itemView.textValue.text = grade.valueSymbol
		if (grade.valueSymbol.isNullOrBlank()) {
			itemView.textValue.isGone = true
		}
	}

	class SectionHeader(private val term: Term, private val grades: List<Grade>) : Section<Grade> {
		override fun getChildItems() = grades
		fun getSectionHeader() = term
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textName = itemView.find<AppCompatTextView>(R.id.termName)
		val textAverage = itemView.find<AppCompatTextView>(R.id.termAverage)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.gradeName)
		val textValue = itemView.find<Chip>(R.id.gradeValue)
	}
}
