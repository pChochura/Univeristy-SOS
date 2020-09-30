package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterTest
import com.pointlessapps.mobileusos.exceptions.ExceptionHttpUnsuccessful
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_tests.view.*

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
					Utils.askForRelogin(requireActivity(), R.string.scopes_missing_description) {
						onForceGoBack?.invoke()
					}
				}
			}
		}
	}

	private fun prepareTestsList() {
		root().listTests.setAdapter(AdapterTest(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentTest(it))
			}
		})
	}
}
