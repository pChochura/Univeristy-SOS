package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.net.Uri
import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEntry
import com.pointlessapps.mobileusos.databinding.FragmentPageBinding
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.fromJson

@Keep
class FragmentPage(private val json: String) :
	FragmentCoreImpl<FragmentPageBinding>(FragmentPageBinding::class.java), FragmentPinnable {

	constructor(page: Chapter.Page, nextPageName: String?) : this(Gson().toJson(page)) {
		this.nextPageName = nextPageName
	}

	private val page = Gson().fromJson<Chapter.Page>(json)
	private var nextPageName: String? = null

	var onNextPageClickListener: (() -> Unit)? = null

	override fun getShortcut(fragment: FragmentCoreImpl<*>, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_guide to page.title.toString())
	}

	override fun created() {
		prepareEntriesList()

		nextPageName?.also {
			binding().buttonNext.isVisible = true
			binding().nextPageName.text = it
		}
		binding().buttonNext.setOnClickListener { onNextPageClickListener?.invoke() }

		binding().pageName.text = page.title.toString()

		if (isPinned(javaClass.name, json)) {
			binding().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		binding().buttonPin.setOnClickListener {
			binding().buttonPin.setIconResource(
				if (togglePin(javaClass.name, json))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun prepareEntriesList() {
		binding().listEntries.apply {
			adapter = AdapterEntry().apply {
				update(page.entries ?: listOf())
				onImageClickListener = {
					startActivity(Intent().apply {
						action = Intent.ACTION_VIEW
						setDataAndType(Uri.parse(it), "image/*")
					})
				}
				onEmailClickListener = {
					onChangeFragment?.invoke(
						FragmentComposeMail(
							recipients = mutableListOf(
								Email.Recipient(
									it,
									null
								)
							)
						)
					)
				}
			}
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}
}
