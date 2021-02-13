package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemCourseChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemCourseClassTypeBinding
import com.pointlessapps.mobileusos.databinding.ListItemCourseHeaderBinding
import com.pointlessapps.mobileusos.models.Course
import com.pointlessapps.mobileusos.models.Term

class AdapterCourse(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterCourse.SectionHeader, List<Course>, AdapterCourse.SectionViewHolder, AdapterCourse.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Course) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemCourseHeaderBinding.inflate(
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
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemCourseChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		courses: List<Course>
	) {
		itemView.binding.courseName.text = courses.firstOrNull()?.courseName.toString()
		itemView.binding.courseGroup.text = context.resources.getQuantityString(
			R.plurals.groups,
			courses.size,
			courses.joinToString { it.groupNumber.toString() }
		)
		courses.distinctBy { it.groupNumber }.singleOrNull()?.also {
			itemView.binding.courseGroup.text =
				context.resources.getQuantityString(R.plurals.groups, 1, it.groupNumber.toString())
		}

		itemView.binding.courseClassTypes.removeAllViews()
		courses.forEach { course ->
			itemView.binding.courseClassTypes.addView(
				ListItemCourseClassTypeBinding.inflate(
					LayoutInflater.from(context),
					null,
					false
				).root.apply {
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

	class SectionViewHolder(val binding: ListItemCourseHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemCourseChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
