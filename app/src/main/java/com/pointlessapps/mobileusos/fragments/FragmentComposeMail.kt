package com.pointlessapps.mobileusos.fragments

import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.addChip
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.fragment_compose_mail.view.*

class FragmentComposeMail(
	private val email: Email? = null,
	private val recipients: MutableList<Email.Recipient> = mutableListOf()
) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_compose_mail

	override fun created() {
		prepareData()
		prepareClickListeners()
		prepareRecipientsList()

		onBackPressedListener = {
			DialogUtil.create(
				requireContext(),
				R.layout.dialog_message,
				{ dialog ->
					dialog.messageMain.text = getString(R.string.discard_email_title)
					dialog.messageSecondary.text = getString(R.string.discard_email_message)
					dialog.buttonPrimary.text = getString(R.string.discard)
					dialog.buttonSecondary.text = getString(R.string.save_draft)

					dialog.buttonPrimary.setOnClickListener {
						dialog.dismiss()
						forceGoBack()
					}
					dialog.buttonSecondary.setOnClickListener {
						dialog.dismiss()
						saveDraft()
						forceGoBack()
					}
				},
				DialogUtil.UNDEFINED_WINDOW_SIZE,
				ViewGroup.LayoutParams.WRAP_CONTENT
			)
			true
		}
	}

	private fun saveDraft() {

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

		recipients.forEach {
			root().listRecipients.addChip(it.name()) { recipients.remove(it) }
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
