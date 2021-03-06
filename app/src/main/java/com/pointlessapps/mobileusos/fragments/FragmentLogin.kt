package com.pointlessapps.mobileusos.fragments

import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.databinding.DialogPickScopesBinding
import com.pointlessapps.mobileusos.databinding.DialogPickUniversityBinding
import com.pointlessapps.mobileusos.databinding.FragmentLoginBinding
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.*
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon

class FragmentLogin : FragmentCoreImpl<FragmentLoginBinding>(FragmentLoginBinding::class.java) {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun created() {
		HelperClientUSOS.registerActivityResultListener(requireActivity() as ComponentActivity)
		prepareClickListeners()
	}

	fun showWelcomeScreen() {
		binding().iconWelcome.isVisible = true
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

							HelperClientUSOS.handleLogin(
								requireActivity() as ComponentActivity,
								university
							) {
								onChangeFragment?.invoke(FragmentBrowser(it))
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

		binding().buttonSelectScopes.setOnClickListener {
			DialogUtil.create(requireContext(), DialogPickScopesBinding::class.java, { dialog ->
				dialog.root.clipToOutline = true

				val prefs = Preferences.get()
				dialog.itemOtherEmails.apply {
					valueSwitch = { prefs.getScopeOtherEmails() }
					onTapped { prefs.putScopeOtherEmails(!prefs.getScopeOtherEmails()) }
				}
				dialog.itemCrsTests.apply {
					valueSwitch = { prefs.getScopeCrsTests() }
					onTapped { prefs.putScopeCrsTests(!prefs.getScopeCrsTests()) }
				}
				dialog.itemMailClient.apply {
					valueSwitch = { prefs.getScopeMailClient() }
					onTapped { prefs.putScopeMailClient(!prefs.getScopeMailClient()) }
				}
				dialog.itemSurveyFilling.apply {
					valueSwitch = { prefs.getScopeSurveyFilling() }
					onTapped { prefs.putScopeSurveyFilling(!prefs.getScopeSurveyFilling()) }
				}
				dialog.itemEvents.apply {
					valueSwitch = { prefs.getScopeEvents() }
					onTapped { prefs.putScopeEvents(!prefs.getScopeEvents()) }
				}
				dialog.buttonOk.setOnClickListener { dismiss() }
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, 500.dp)
		}
	}
}
