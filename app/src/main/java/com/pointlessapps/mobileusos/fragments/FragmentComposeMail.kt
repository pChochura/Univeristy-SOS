package com.pointlessapps.mobileusos.fragments

import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.addChip
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_compose_mail.view.*

class FragmentComposeMail(private val email: Email? = null) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val recipients = mutableListOf<Email.Recipient>()

	override fun getLayoutId() = R.layout.fragment_compose_mail

	override fun created() {
		prepareData()
		prepareClickListeners()
		prepareRecipientsList()
	}

	private fun prepareRecipientsList() {
		root().inputRecipients.apply {
			setAdapter(AdapterAutocomplete(requireContext()).apply {
				onItemClickListener =
					AdapterView.OnItemClickListener { _, _, position, _ ->
						recipients.add(list[position])
						root().inputRecipients.text.clear()
						root().listRecipients.addChip(list[position].name()) {
							recipients.remove(list[position])
						}
					}

				isLongClickable = false
			})

			addTextChangedListener {
				viewModelUser.getUsersByQuery(it.toString())
					.observe(this@FragmentComposeMail) { list ->
						(adapter as? AdapterAutocomplete)?.update(list?.map { user ->
							Email.Recipient(
								null,
								user
							)
						} ?: listOf())
					}
			}
		}
	}

	private fun prepareData() {
		email?.also {
			root().inputSubject.setText(it.subject ?: "")
			root().inputContent.setText(it.content ?: "")

			viewModelUser.getEmailRecipients(it.id).observe(this) { recipients ->
				this.recipients.apply {
					clear()
					addAll(recipients ?: listOf())
				}
				recipients?.forEach { recipient ->
					root().listRecipients.addChip(recipient.name()) {
						this.recipients.remove(recipient)
					}
				}
			}
		}
	}

	private fun prepareClickListeners() {
		root().buttonSend.setOnClickListener {
			// TODO: send email
		}
	}
}
