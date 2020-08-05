package com.pointlessapps.mobileusos.fragments

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Article
import kotlinx.android.synthetic.main.fragment_article.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentArticle(private val article: Article) : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_article

	override fun created() {
		root().articleHeadline.text = parseHtml(article.headlineHtml.toString())
		root().articleContent.text = parseHtml(article.contentHtml.toString())
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

	private fun parseHtml(input: String) =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Html.fromHtml(input, Html.FROM_HTML_MODE_COMPACT)
		} else {
			Html.fromHtml(input)
		}
}
