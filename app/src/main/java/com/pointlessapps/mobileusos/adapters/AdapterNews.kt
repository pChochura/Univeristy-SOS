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
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Utils
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

class AdapterNews(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterNews.SectionHeader, Article, AdapterNews.SectionViewHolder, AdapterNews.ChildViewHolder>(
		context,
		sections
	) {

	private val wholeList: MutableList<SectionHeader> = sections.toMutableList()
	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

	lateinit var onClickListener: (Article) -> Unit

	init {
		setHasStableIds(true)
	}

	override fun getItemId(position: Int) = position.toLong()

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_article_header, itemView, false)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.textName.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			LayoutInflater.from(context).inflate(R.layout.list_item_article_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		article: Article
	) {
		itemView.bg.setOnClickListener {
			onClickListener(article)
		}

		itemView.textName.text = Utils.stripHtmlTags(article.headlineHtml.toString())
		itemView.textDescription.text =
			ellipsize(Utils.stripHtmlTags(article.contentHtml.toString()))
		itemView.textDate.text = dateFormat.format(article.publicationDate ?: return)
		itemView.textCategory.text = article.category?.name?.toString()
		itemView.textCategory.isGone = article.category?.name?.toString().isNullOrBlank()
	}

	private fun ellipsize(text: String): String {
		val shorten = text.take(150)
		val index = shorten.lastIndexOf(' ')
		return shorten.take(if (index < 130) 150 else index).plus("…")
	}

	class SectionHeader(private val header: String, private val courses: List<Article>) :
		Section<Article> {
		override fun getChildItems() = courses
		fun getSectionHeader() = header
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textName = itemView.find<AppCompatTextView>(R.id.headerName)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.articleName)
		val textDescription = itemView.find<AppCompatTextView>(R.id.articleDescription)
		val textDate = itemView.find<Chip>(R.id.articleDate)
		val textCategory = itemView.find<Chip>(R.id.articleCategory)
	}

	fun update(list: List<SectionHeader>) {
		wholeList.apply {
			clear()
			addAll(list)
		}
		notifyDataChanged(list)
	}

	fun filterCategories(categories: List<Article.Category>) {
		if (categories.isEmpty()) {
			notifyDataChanged(wholeList)

			return
		}

		notifyDataChanged(mutableListOf(*wholeList.toTypedArray()).map {
			SectionHeader(
				it.getSectionHeader(),
				it.childItems.filter { item ->
					categories.find { category ->
						category.id == item.category?.id ||
								category.id == "default" && item.category?.id.isNullOrEmpty()
					} !== null
				}
			)
		}.filter { it.childItems.isNotEmpty() })
	}
}
