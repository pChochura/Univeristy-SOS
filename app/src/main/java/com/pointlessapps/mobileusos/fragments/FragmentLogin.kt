package com.pointlessapps.mobileusos.fragments

import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.databinding.DialogPickUniversityBinding
import com.pointlessapps.mobileusos.databinding.FragmentLoginBinding
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon

class FragmentLogin : FragmentCoreImpl<FragmentLoginBinding>(FragmentLoginBinding::class.java) {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun created() {
		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		binding().buttonLogin.setOnClickListener {
			DialogUtil.create(requireContext(), DialogPickUniversityBinding::class.java, { dialog ->
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

							dismiss()
						}
					})
				}

				viewModelCommon.getAllUniversities().observe(this@FragmentLogin) {
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

				dialog.inputSearchUniversities.addTextChangedListener {
					(dialog.listUniversities.adapter as? AdapterUniversity)?.showMatching(
						it.toString()
					)
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, 500.dp)
		}
	}
}
