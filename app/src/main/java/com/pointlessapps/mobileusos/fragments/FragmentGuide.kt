package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterChapter
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.fragment_guide.view.*
import java.util.*

class FragmentGuide : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()
	private var chapters: List<Chapter>? = null

	override fun getLayoutId() = R.layout.fragment_guide

	override fun created() {
		prepareChaptersList()

		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelCommon.getWholeGuide().observe(this) { (chapters) ->
			this.chapters = chapters

			(root().listChapters.adapter as? AdapterChapter)?.notifyDataChanged(
				chapters.map {
					AdapterChapter.SectionHeader(
						it.title.toString().toLowerCase(Locale.getDefault()).capitalize(
							Locale.getDefault()
						), it.pages ?: listOf()
					)
				}
			)
		}.onFinished {
			root().listChapters.setLoaded(true)
			root().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareChaptersList() {
		root().listChapters.setAdapter(AdapterChapter(requireContext()).apply {
			onClickListener = { page, sectionPos, childPos ->
				onChangeFragment?.invoke(preparePageFragment(page, sectionPos, childPos))
			}
		})
	}

	private fun preparePageFragment(
		page: Chapter.Page,
		sectionPos: Int,
		childPos: Int
	): FragmentBaseInterface = chapters?.getOrNull(sectionPos)?.pages?.getOrNull(childPos + 1).run {
		FragmentPage(
			page,
			this?.title?.toString()
		).apply {
			onNextPageClickListener = {
				this@run?.also {
					onReplaceFragment?.invoke(
						preparePageFragment(
							this@run,
							sectionPos,
							childPos + 1
						)
					)
				}
			}
		}
	}
}
