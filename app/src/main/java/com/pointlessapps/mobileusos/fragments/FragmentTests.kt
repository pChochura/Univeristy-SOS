package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.adapters.AdapterTest
import com.pointlessapps.mobileusos.exceptions.ExceptionHttpUnsuccessful
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.fromJson
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.fragment_tests.view.*
import org.jetbrains.anko.doAsync

class FragmentTests : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_tests

	override fun created() {
		prepareTestsList()

		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelUser.getAllTests().observe(this) { (exams) ->
			(root().listTests.adapter as? AdapterTest)?.notifyDataChanged(
				exams.groupBy { it.courseEdition.term!! }
					.toSortedMap(Term::compareTo)
					.map {
						AdapterTest.SectionHeader(
							it.key.name.toString(),
							it.value
						)
					}
			)
		}.onFinished {
			root().listTests.setLoaded(true)
			root().pullRefresh.isRefreshing = false

			if (it is ExceptionHttpUnsuccessful) {
				val message = Gson().fromJson<Map<String, String>>(it.message ?: "{}")
				if (message["reason"] == "scope_missing") {
					askForRelogin()
				}
			}
		}
	}

	private fun askForRelogin() {
		DialogUtil.create(
			object : DialogUtil.StatefulDialog() {
				override fun toggle() {
					dialog.progressBar.isVisible = true
					dialog.messageMain.setText(R.string.loading)
					dialog.messageSecondary.isGone = true
					dialog.buttonPrimary.isGone = true
					dialog.buttonSecondary.isGone = true
				}
			},
			requireContext(), R.layout.dialog_loading, { dialog ->
				dialog.messageMain.setText(R.string.there_been_a_problem)
				dialog.messageSecondary.setText(R.string.scopes_missing_description)
				dialog.buttonPrimary.setText(R.string.logout)
				dialog.buttonPrimary.setOnClickListener {
					toggle()
					doAsync {
						Preferences.get().clear()
						AppDatabase.init(requireContext()).clearAllTables()
						dialog.dismiss()
						requireActivity().apply {
							startActivity(
								Intent(
									requireContext(),
									ActivityLogin::class.java
								)
							)
							finish()
						}
					}
				}
				dialog.buttonSecondary.setOnClickListener {
					dialog.dismiss()
					onForceGoBack?.invoke()
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT
		)
	}

	private fun prepareTestsList() {
		root().listTests.setAdapter(AdapterTest(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentTest(it))
			}
		})
	}
}
