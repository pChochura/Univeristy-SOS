package com.pointlessapps.mobileusos.fragments

import android.text.method.LinkMovementMethod
import android.view.View
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Article
import com.pointlessapps.mobileusos.utils.Utils
import kotlinx.android.synthetic.main.fragment_article.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentArticle(private val article: Article) : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_article

	override fun created() {
		root().articleHeadline.text = Utils.parseHtml(article.headlineHtml.toString())
		root().articleContent.text = Utils.parseHtml(article.contentHtml.toString())
		root().articleAuthor.text = article.author

		article.publicationDate?.also {
			root().articleDate.visibility = View.VISIBLE
			root().articleDate.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it)
		}
		article.category?.name?.toString()?.takeUnless { it.isEmpty() }?.also {
			root().articleCategory.visibility = View.VISIBLE
			root().articleCategory.text = it
		}

		root().articleContent.movementMethod = LinkMovementMethod.getInstance()
	}
}
