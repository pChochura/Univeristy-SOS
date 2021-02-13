package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemTestChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemTestHeaderBinding
import com.pointlessapps.mobileusos.models.Test

class AdapterTest(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterTest.SectionHeader, Test, AdapterTest.SectionViewHolder, AdapterTest.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Test) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemTestHeaderBinding.inflate(
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
		itemView.binding.testHeader.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemTestChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		test: Test
	) {
		itemView.binding.root.setOnClickListener {
			onClickListener.invoke(test)
		}

		itemView.binding.courseName.text = test.courseEdition.course?.name.toString()
		if (!test.isLimitedToGroups || test.classGroups == null) {
			itemView.binding.groups.text = context.getString(R.string.all_participants)
		} else {
			itemView.binding.groups.text = context.getString(
				R.string.groups_other,
				test.classGroups!!.joinToString { it.number.toString() })
		}
	}

	class SectionHeader(private val header: String, private val tests: List<Test>) :
		Section<Test> {
		override fun getChildItems() = tests
		fun getSectionHeader() = header
	}

	class SectionViewHolder(val binding: ListItemTestHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemTestChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
