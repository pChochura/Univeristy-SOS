package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterSurvey
import com.pointlessapps.mobileusos.databinding.FragmentSurveysBinding
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getScopeMailClient
import com.pointlessapps.mobileusos.helpers.getScopeSurveyFilling
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser

class FragmentSurveys :
	FragmentCoreImpl<FragmentSurveysBinding>(FragmentSurveysBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun created() {
		checkScopes() ?: return

		prepareSurveysList()

		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		checkScopes() ?: return

		viewModelUser.getSurveysToFill().observe(this) { (list) ->
			binding().listSurveys.setEmptyText(getString(R.string.no_surveys_to_fill))
			binding().listSurveys.setEmptyIcon(R.drawable.ic_no_surveys)
			binding().listSurveys.setLoaded(false)
			(binding().listSurveys.adapter as? AdapterSurvey)?.notifyDataChanged(
				list.groupBy(Survey::didIFillOut)
					.toSortedMap { section1, section2 -> if (section1 == true) 1 else if (section2 == true) -1 else 0 }
					.map {
						AdapterSurvey.SectionHeader(
							if (it.key == true) getString(R.string.filled_out) else getString(
								R.string.to_fill_out
							),
							it.value.sortedBy(Survey::id)
						)
					}
			)
		}.onFinished {
			binding().listSurveys.setLoaded(true)
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun checkScopes(): Boolean? {
		if (!Preferences.get().getScopeSurveyFilling()) {
			Utils.askForRelogin(requireActivity(), R.string.scopes_missing_description) {
				onForceGoBack?.invoke()
			}

			return null
		}

		return true
	}

	private fun prepareSurveysList() {
		binding().listSurveys.setAdapter(AdapterSurvey(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentSurvey(it))
			}
		})
	}
}
