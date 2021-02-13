package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.databinding.ListItemArticleChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemArticleHeaderBinding
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Utils
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
			ListItemArticleHeaderBinding.inflate(
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
		itemView.binding.headerName.text = section.getSectionHeader()
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemArticleChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		article: Article
	) {
		itemView.binding.root.setOnClickListener {
			onClickListener(article)
		}

		itemView.binding.articleName.text = Utils.stripHtmlTags(article.headlineHtml.toString())
		itemView.binding.articleDescription.text =
			ellipsize(Utils.stripHtmlTags(article.contentHtml.toString()))
		itemView.binding.articleDate.text = dateFormat.format(article.publicationDate ?: return)
		itemView.binding.articleCategory.text = article.category?.name?.toString()
		itemView.binding.articleCategory.isGone = article.category?.name?.toString().isNullOrBlank()
	}

	private fun ellipsize(text: String): String {
		val shorten = text.take(150)
		val index = shorten.lastIndexOf(' ')
		return shorten.take(if (index < 130) 150 else index).plus("…")
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

	class SectionHeader(private val header: String, private val courses: List<Article>) :
		Section<Article> {
		override fun getChildItems() = courses
		fun getSectionHeader() = header
	}

	class SectionViewHolder(val binding: ListItemArticleHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemArticleChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
