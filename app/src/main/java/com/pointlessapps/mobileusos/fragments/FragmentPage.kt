package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEntry
import com.pointlessapps.mobileusos.models.Chapter
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.fromJson
import kotlinx.android.synthetic.main.fragment_page.view.*

class FragmentPage(private val json: String) : FragmentBase(), FragmentPinnable {

	constructor(page: Chapter.Page) : this(Gson().toJson(page))

	private val page = Gson().fromJson<Chapter.Page>(json)

	override fun getLayoutId() = R.layout.fragment_page

	override fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_guide to page.title.toString())
	}

	override fun created() {
		prepareEntriesList()

		root().pageName.text = page.title.toString()

		if (isPinned(javaClass.name, json)) {
			root().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		root().buttonPin.setOnClickListener {
			root().buttonPin.setIconResource(
				if (togglePin(javaClass.name, json))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun prepareEntriesList() {
		root().listEntries.apply {
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
