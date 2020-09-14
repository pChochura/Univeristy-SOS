package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterSurvey
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_surveys.view.*

class FragmentSurveys : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_surveys

	override fun created() {
		prepareSurveysList()

		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelUser.getSurveysToFill().observe(this) { (list) ->
			root().listSurveys.setEmptyText(getString(R.string.no_surveys_to_fill))
			root().listSurveys.setEmptyIcon(R.drawable.ic_no_surveys)
			root().listSurveys.setLoaded(false)
			(root().listSurveys.adapter as? AdapterSurvey)?.notifyDataChanged(
				list.groupBy(Survey::didIFillOut)
					.toSortedMap { section1, section2 -> if (section1 == true) 1 else if (section2 == true) -1 else 0 }
					.map {
						AdapterSurvey.SectionHeader(
							if (it.key == true) getString(R.string.filled_out) else getString(
								R.string.to_fill_out
							),
							it.value
						)
					}
			)
		}.onFinished {
			root().listSurveys.setLoaded(true)
			root().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareSurveysList() {
		root().listSurveys.setAdapter(AdapterSurvey(requireContext()).apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentSurvey(it))
			}
		})
	}
}
