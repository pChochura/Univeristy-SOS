package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEmail
import com.pointlessapps.mobileusos.databinding.FragmentMailsBinding
import com.pointlessapps.mobileusos.viewModels.ViewModelUser

class FragmentMails : FragmentCoreImpl<FragmentMailsBinding>(FragmentMailsBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getNavigationIcon() = R.drawable.ic_mail
	override fun getNavigationName() = R.string.mail

	override fun created() {
		prepareEmailsList()
		prepareClickListeners()

		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelUser.getAllEmails().observe(this) { (emails) ->
			binding().listEmails.setEmptyText(getString(R.string.no_emails))
			binding().listEmails.setLoaded(false)
			(binding().listEmails.adapter as? AdapterEmail)?.update(emails)
		}.onFinished {
			binding().listEmails.setLoaded(true)
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareEmailsList() {
		binding().listEmails.setAdapter(AdapterEmail().apply {
			onClickListener = {
				onChangeFragment?.invoke(FragmentMail(it))
			}
		})
	}

	private fun prepareClickListeners() {
		binding().buttonAdd.setOnClickListener {
			onChangeFragment?.invoke(FragmentComposeMail())
		}
	}
}
