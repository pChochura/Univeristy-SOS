package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.databinding.ListItemChapterChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemChapterHeaderBinding
import com.pointlessapps.mobileusos.models.Chapter

class AdapterChapter(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterChapter.SectionHeader, Chapter.Page, AdapterChapter.SectionViewHolder, AdapterChapter.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Chapter.Page, Int, Int) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemChapterHeaderBinding.inflate(
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
		itemView.binding.chapterHeader.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemChapterChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		page: Chapter.Page
	) {
		itemView.binding.root.setOnClickListener {
			onClickListener.invoke(page, sectionPosition, childPosition)
		}

		itemView.binding.pageName.text = page.title.toString()
	}

	class SectionHeader(private val header: String, private val pages: List<Chapter.Page>) :
		Section<Chapter.Page> {
		override fun getChildItems() = pages
		fun getSectionHeader() = header
	}

	class SectionViewHolder(val binding: ListItemChapterHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemChapterChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
