package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
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
	}

	private fun prepareEmailsList() {
		root().listEmails.setAdapter(AdapterEmail().apply {
			onClickListener = {
				onChangeFragmentListener?.invoke(FragmentMail(it))
			}
		})

		viewModelUser.getAllEmails().observe(this) { (emails, online) ->
			root().listEmails.setEmptyText(getString(R.string.no_emails))
			root().listEmails.setLoaded(online)
			(root().listEmails.adapter as? AdapterEmail)?.update(emails)
		}
	}

	private fun prepareClickListeners() {
		root().buttonAdd.setOnClickListener {
			onChangeFragmentListener?.invoke(FragmentComposeMail())
		}
	}
}
