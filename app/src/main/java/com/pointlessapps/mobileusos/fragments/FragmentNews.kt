package com.pointlessapps.mobileusos.fragments

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterNews
import com.pointlessapps.mobileusos.databinding.FragmentNewsBinding
import com.pointlessapps.mobileusos.databinding.ListItemArticleCategoryBinding
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentNews : FragmentCoreImpl<FragmentNewsBinding>(FragmentNewsBinding::class.java) {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	private val selectedCategories = mutableListOf<Article.Category>()

	override fun getNavigationIcon() = R.drawable.ic_news
	override fun getNavigationName() = R.string.news

	override fun created() {
		prepareNewsList()
		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		binding().horizontalProgressBar.isRefreshing = true

		val lastWeekDate = Calendar.getInstance().apply {
			add(Calendar.DAY_OF_MONTH, -7)
		}.timeInMillis

		val loaded = CountDownLatch(2)

		viewModelCommon.getAllNews().observe(this) { (news) ->
			(binding().listNews.adapter as? AdapterNews)?.update(
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

			binding().listNews.setEmptyText(getString(R.string.no_new_articles))
		}.onFinished { loaded.countDown() }

		prepareCategoriesList { loaded.countDown() }

		doAsync {
			loaded.await()
			binding().pullRefresh.isRefreshing = false
			binding().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareNewsList() {
		binding().listNews.setAdapter(AdapterNews(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentArticle(it))
			}
		})
	}

	private fun prepareCategoriesList(callback: (() -> Unit)? = null) {
		viewModelCommon.getAllNewsCategories().observe(this) { (list) ->
			binding().listCategories.removeAllViews()
			LayoutInflater.from(requireContext()).let { inflater ->
				list.sorted().forEach { category ->
					binding().listCategories.apply {
						addView(
							ListItemArticleCategoryBinding.inflate(inflater, null, false)
								.root.apply {
									text = category.name?.toString()
									setOnCheckedChangeListener { _, checked ->
										if (checked) {
											selectedCategories.add(category)
										} else {
											selectedCategories.remove(category)
										}

										(binding().listNews.adapter as? AdapterNews)?.filterCategories(
											selectedCategories
										)
									}
								}
						)
					}
				}
			}

			callback?.invoke()
		}
	}
}
