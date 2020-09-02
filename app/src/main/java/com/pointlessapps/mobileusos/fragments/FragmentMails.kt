package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEmail
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_mails.view.*

class FragmentMails : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_mails
	override fun getNavigationIcon() = R.drawable.ic_mail
	override fun getNavigationName() = R.string.mail

	override fun created() {
		prepareEmailsList()
		prepareClickListeners()

		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		viewModelUser.getAllEmails().observe(this) { (emails) ->
			root().listEmails.setEmptyText(getString(R.string.no_emails))
			root().listEmails.setLoaded(false)
			(root().listEmails.adapter as? AdapterEmail)?.update(emails)
		}.onFinished {
			root().listEmails.setLoaded(true)
			root().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareEmailsList() {
		root().listEmails.setAdapter(AdapterEmail().apply {
			onClickListener = {
				onChangeFragmentListener?.invoke(FragmentMail(it))
			}
		})
	}

	private fun prepareClickListeners() {
		root().buttonAdd.setOnClickListener {
			onChangeFragmentListener?.invoke(FragmentComposeMail())
		}
	}
}
