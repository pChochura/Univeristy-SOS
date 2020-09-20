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
import com.pointlessapps.mobileusos.models.Chapter
import org.jetbrains.anko.find

class AdapterChapter(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterChapter.SectionHeader, Chapter.Page, AdapterChapter.SectionViewHolder, AdapterChapter.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Chapter.Page) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_chapter_header, itemView, false)
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
			LayoutInflater.from(context).inflate(R.layout.list_item_chapter_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		page: Chapter.Page
	) {
		itemView.bg.setOnClickListener {
			onClickListener.invoke(page)
		}

		itemView.textName.text = page.title.toString()
	}

	class SectionHeader(private val header: String, private val pages: List<Chapter.Page>) :
		Section<Chapter.Page> {
		override fun getChildItems() = pages
		fun getSectionHeader() = header
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textHeader = itemView.find<AppCompatTextView>(R.id.chapterHeader)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.pageName)
	}
}
