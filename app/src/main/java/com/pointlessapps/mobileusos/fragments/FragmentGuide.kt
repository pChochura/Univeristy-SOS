package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.adapters.AdapterChapter
import com.pointlessapps.mobileusos.databinding.FragmentGuideBinding
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import java.util.*

class FragmentGuide : FragmentCoreImpl<FragmentGuideBinding>(FragmentGuideBinding::class.java) {

	private val viewModelCommon by viewModels<ViewModelCommon>()
	private var chapters: List<Chapter>? = null

	override fun created() {
		prepareChaptersList()

		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelCommon.getWholeGuide().observe(this) { (chapters) ->
			this.chapters = chapters

			(binding().listChapters.adapter as? AdapterChapter)?.notifyDataChanged(
				chapters.map {
					AdapterChapter.SectionHeader(
						it.title.toString().toLowerCase(Locale.getDefault()).capitalize(
							Locale.getDefault()
						), it.pages ?: listOf()
					)
				}
			)
		}.onFinished {
			binding().listChapters.setLoaded(true)
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareChaptersList() {
		binding().listChapters.setAdapter(AdapterChapter(requireContext()).apply {
			onClickListener = { page, sectionPos, childPos ->
				onChangeFragment?.invoke(preparePageFragment(page, sectionPos, childPos))
			}
		})
	}

	private fun preparePageFragment(
		page: Chapter.Page,
		sectionPos: Int,
		childPos: Int
	): FragmentPage = chapters?.getOrNull(sectionPos)?.pages?.getOrNull(childPos + 1).run {
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
