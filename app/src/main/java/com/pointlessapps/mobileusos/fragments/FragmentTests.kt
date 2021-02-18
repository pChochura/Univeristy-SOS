package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterTest
import com.pointlessapps.mobileusos.databinding.FragmentTestsBinding
import com.pointlessapps.mobileusos.exceptions.ExceptionHttpUnsuccessful
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getScopeCrsTests
import com.pointlessapps.mobileusos.helpers.getScopeSurveyFilling
import com.pointlessapps.mobileusos.models.Term
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.utils.fromJson
import com.pointlessapps.mobileusos.viewModels.ViewModelUser

class FragmentTests : FragmentCoreImpl<FragmentTestsBinding>(FragmentTestsBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun created() {
		checkScopes() ?: return

		prepareTestsList()

		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		checkScopes() ?: return

		viewModelUser.getAllTests().observe(this) { (exams) ->
			(binding().listTests.adapter as? AdapterTest)?.notifyDataChanged(
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
			binding().listTests.setLoaded(true)
			binding().pullRefresh.isRefreshing = false

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

	private fun checkScopes(): Boolean? {
		if (!Preferences.get().getScopeCrsTests()) {
			Utils.askForRelogin(requireActivity(), R.string.scopes_missing_description) {
				onForceGoBack?.invoke()
			}

			return null
		}

		return true
	}

	private fun prepareTestsList() {
		binding().listTests.setAdapter(AdapterTest(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentTest(it))
			}
		})
	}
}
