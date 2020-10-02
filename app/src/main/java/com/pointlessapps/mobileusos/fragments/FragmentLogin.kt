package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.managers.SearchManager
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.dialog_pick_university.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class FragmentLogin : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun getLayoutId() = R.layout.fragment_login

	override fun created() {
		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		root().buttonLogin.setOnClickListener {
			DialogUtil.create(requireContext(), R.layout.dialog_pick_university, { dialog ->
				dialog.listUniversities.apply {
					setAdapter(AdapterUniversity().apply {
						onClickListener = { university ->
							if (university.consumerKey == null || university.consumerSecret == null) {
								throw ExceptionNullKeyOrSecret("Neither consumerKey nor consumerSecret can be null.")
							}

							activity?.apply {
								HelperClientUSOS.handleLogin(this, university) {
									onChangeFragment?.invoke(FragmentBrowser(it))
								}
							}

							dialog.dismiss()
						}
					})
				}

				viewModelCommon.getAllUniversities().observe(this) {
					(dialog.listUniversities.adapter as? AdapterUniversity)?.apply {
						update(it.first)
						showMatching(dialog.inputSearchUniversities.text.toString())
					}

					dialog.listUniversities.apply {
						setEmptyText(getString(R.string.no_universities))
						setEmptyIcon(R.drawable.ic_no_universities)
						setLoaded(false)
					}
				}.onFinished {
					dialog.listUniversities.setLoaded(true)
				}

				SearchManager.of(dialog.inputSearchUniversities).setOnChangeTextListener {
					(dialog.listUniversities.adapter as? AdapterUniversity)?.showMatching(it)
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, 500.dp)
		}
	}
}
