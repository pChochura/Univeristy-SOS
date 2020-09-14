package com.pointlessapps.mobileusos.fragments

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterNews
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.fragment_news.view.*
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentNews : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	private val selectedCategories = mutableListOf<Article.Category>()

	override fun getLayoutId() = R.layout.fragment_news
	override fun getNavigationIcon() = R.drawable.ic_news
	override fun getNavigationName() = R.string.news

	override fun created() {
		prepareNewsList()
		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		root().horizontalProgressBar.isRefreshing = true

		val lastWeekDate = Calendar.getInstance().apply {
			add(Calendar.DAY_OF_MONTH, -7)
		}.timeInMillis

		val loaded = CountDownLatch(2)

		viewModelCommon.getAllNews().observe(this) { (news) ->
			(root().listNews.adapter as? AdapterNews)?.update(
				news.filter { article -> article.headlineHtml != null && article.contentHtml != null }
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
		}.onFinished { loaded.countDown() }

		prepareCategoriesList { loaded.countDown() }

		doAsync {
			loaded.await()
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareNewsList() {
		root().listNews.setAdapter(AdapterNews(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentArticle(it))
			}
		})
	}

	private fun prepareCategoriesList(callback: (() -> Unit)? = null) {
		viewModelCommon.getAllNewsCategories().observe(this) { (list) ->
			root().listCategories.removeAllViews()
			list.forEach { category ->
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

			callback?.invoke()
		}
	}
}
