package com.pointlessapps.mobileusos.fragments

import android.text.method.LinkMovementMethod
import android.view.View
import com.pointlessapps.mobileusos.databinding.FragmentArticleBinding
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class FragmentArticle(private val article: Article) : FragmentCoreImpl<FragmentArticleBinding>(FragmentArticleBinding::class.java) {

	override fun created() {
		binding().articleHeadline.text = Utils.parseHtml(article.headlineHtml.toString())
		binding().articleContent.text = Utils.parseHtml(article.contentHtml.toString())
		binding().articleAuthor.text = article.author

		article.publicationDate?.also {
			binding().articleDate.visibility = View.VISIBLE
			binding().articleDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
		}
		article.category?.name?.toString()?.takeUnless { it.isEmpty() }?.also {
			binding().articleCategory.visibility = View.VISIBLE
			binding().articleCategory.text = it
		}

		binding().articleContent.movementMethod = LinkMovementMethod.getInstance()
	}
}
