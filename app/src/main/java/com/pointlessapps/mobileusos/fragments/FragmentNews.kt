package com.pointlessapps.mobileusos.fragments

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterNews
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.fragment_news.view.*
import java.util.*

class FragmentNews : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	private val selectedCategories = mutableListOf<Article.Category>()

	override fun getLayoutId() = R.layout.fragment_news
	override fun getNavigationIcon() = R.drawable.ic_news
	override fun getNavigationName() = R.string.news

	override fun created() {
		prepareNewsList()
		prepareCategoriesList()
	}

	private fun prepareNewsList() {
		root().listNews.setAdapter(AdapterNews(requireContext()).apply {
			onClickListener = {
				onChangeFragmentListener?.invoke(FragmentArticle(it))
			}
		})

		val lastWeekDate = Calendar.getInstance().apply {
			add(Calendar.DAY_OF_MONTH, -7)
		}.timeInMillis

		viewModelCommon.getAllNews().observe(this) {
			(root().listNews.adapter as AdapterNews).update(
				it.filter { article -> article.headlineHtml != null && article.contentHtml != null }
					.groupBy { article ->
						(article.publicationDate?.time ?: lastWeekDate) - lastWeekDate <= 0
					}
					.map { entry ->
						AdapterNews.SectionHeader(
							getString(if (entry.key) R.string.older else R.string.last_week),
							entry.value
						)
					}
			)

			root().listNews.setEmptyText(getString(R.string.no_new_articles))
		}
	}

	private fun prepareCategoriesList() {
		viewModelCommon.getAllNewsCategories().observe(this) {
			root().listCategories.removeAllViews()
			it.forEach { category ->
				root().listCategories.apply {
					addView(
						(LayoutInflater.from(requireContext())
							.inflate(R.layout.list_item_article_category, null) as Chip).apply {
							text = category.name?.toString()

							setOnCheckedChangeListener { _, checked ->
								if (checked) {
									selectedCategories.add(category)
								} else {
									selectedCategories.remove(category)
								}

								(root().listNews.adapter as? AdapterNews)?.filterCategories(
									selectedCategories
								)
							}
						})
				}
			}
		}
	}
}
