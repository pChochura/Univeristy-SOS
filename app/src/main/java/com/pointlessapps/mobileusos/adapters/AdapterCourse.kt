package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Term
import org.jetbrains.anko.find

class AdapterCourse(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterCourse.SectionHeader, List<Course>, AdapterCourse.SectionViewHolder, AdapterCourse.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Course) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_course_header, itemView, false)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.textName.text =
			(section.getSectionHeader().name ?: section.getSectionHeader().id).toString()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_course_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		courses: List<Course>
	) {
		courses.firstOrNull()?.also {
			itemView.textName.text = it.courseName.toString()
			itemView.textGroup.text = context.getString(R.string.group_number, it.groupNumber)
		}

		itemView.groupClassTypes.removeAllViews()
		courses.forEach { course ->
			itemView.groupClassTypes.addView(
				(LayoutInflater.from(context)
					.inflate(
						R.layout.list_item_course_class_type,
						null
					) as Chip).apply {
					setOnClickListener {
						onClickListener(course)
					}

					text = course.classType.toString()
				}
			)
		}
	}


	class SectionHeader(private val term: Term, private val courses: List<List<Course>>) :
		Section<List<Course>> {
		override fun getChildItems() = courses
		fun getSectionHeader() = term
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textName = itemView.find<AppCompatTextView>(R.id.termName)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textName = itemView.find<AppCompatTextView>(R.id.courseName)
		val textGroup = itemView.find<AppCompatTextView>(R.id.courseGroup)
		val groupClassTypes = itemView.find<ChipGroup>(R.id.courseClassTypes)
	}
}
