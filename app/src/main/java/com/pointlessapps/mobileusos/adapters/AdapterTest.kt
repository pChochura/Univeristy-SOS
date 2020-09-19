package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Test
import org.jetbrains.anko.find

class AdapterTest(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterTest.SectionHeader, Test, AdapterTest.SectionViewHolder, AdapterTest.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Test) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_test_header, itemView, false)
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
			LayoutInflater.from(context).inflate(R.layout.list_item_test_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		test: Test
	) {
		itemView.bg.setOnClickListener {
			onClickListener.invoke(test)
		}

		itemView.textName.text = test.courseEdition.course?.name.toString()
		if (!test.isLimitedToGroups || test.classGroups == null) {
			itemView.textGroups.text = context.getString(R.string.all_participants)
		} else {
			itemView.textGroups.text = context.getString(
				R.string.groups_other,
				test.classGroups!!.joinToString { it.number.toString() })
		}
	}

	class SectionHeader(private val header: String, private val tests: List<Test>) :
		Section<Test> {
		override fun getChildItems() = tests
		fun getSectionHeader() = header
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textHeader = itemView.find<AppCompatTextView>(R.id.examHeader)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.courseName)
		val textGroups = itemView.find<AppCompatTextView>(R.id.groups)
	}
}
