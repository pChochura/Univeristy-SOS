package com.pointlessapps.mobileusos.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.addChip
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.fragment_compose_mail.view.*


class FragmentComposeMail(
	private val email: Email? = null,
	private val recipients: MutableList<Email.Recipient> = mutableListOf()
) : FragmentBase() {

	companion object {
		const val PICK_FILE_REQUEST_CODE = 1243
	}

	private val viewModelUser by viewModels<ViewModelUser>()
	private val attachments = mutableListOf<Email.Attachment>()

	override fun getLayoutId() = R.layout.fragment_compose_mail

	override fun created() {
		prepareData()
		prepareClickListeners()
		prepareRecipientsList()
		prepareRecipientsEditorListener()
		prepareAttachmentsList()

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

	private fun prepareRecipientsEditorListener() {
		root().inputRecipients.setOnKeyListener { _, keyCode, keyEvent ->
			if (keyEvent.action == KeyEvent.ACTION_DOWN) {
				when (keyCode) {
					KeyEvent.KEYCODE_DEL -> {
						if (root().inputRecipients.text.isNullOrEmpty() && recipients.isNotEmpty()) {
							recipients.removeAt(recipients.lastIndex)
							root().listRecipients.removeViewAt(root().listRecipients.childCount - 2)

							return@setOnKeyListener true
						}
					}
					KeyEvent.KEYCODE_ENTER -> {
						val recipient =
							Email.Recipient(root().inputRecipients.text.toString(), null)
						recipients.add(recipient)
						root().listRecipients.addChip(recipient.email!!) {
							recipients.remove(recipient)
						}

						root().inputRecipients.text.clear()
						return@setOnKeyListener true
					}
				}
			}

			return@setOnKeyListener false
		}
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

	private fun prepareAttachmentsList() {
		root().listAttachments.apply {
			adapter = AdapterAttachment(true).apply {
				onClickListener = {

				}
				onAddClickListener = {
					val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
					intent.type = "*/*"
					intent.addCategory(Intent.CATEGORY_DEFAULT)
					startActivityForResult(
						Intent.createChooser(
							intent,
							getString(R.string.choose_an_attachment)
						), PICK_FILE_REQUEST_CODE
					)
				}
			}
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareData() {
		email?.also {
			root().inputSubject.setText(it.subject ?: "")
			root().inputContent.setText(it.content ?: "")

			attachments.addAll(it.attachments ?: listOf())

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

	private fun saveDraft() {
		if (email == null) {
			viewModelUser.createEmail(
				root().inputSubject.text.toString(),
				root().inputContent.text.toString()
			) { id ->
				if (id == null) {
					return@createEmail
				}

				val parts = recipients.partition { it.user !== null }
				viewModelUser.updateEmailRecipients(
					id,
					parts.first.map { it.user!!.id },
					parts.second.map { it.email!! }
				) { }

				attachments.forEach { attachment ->
//					viewModelUser.addEmailAttachment(id, data, attachment.filename!!) {
//						attachment.id = it ?: return@addEmailAttachment
//					}
				}
			}
		}
	}

	private fun prepareClickListeners() {
		root().buttonSend.setOnClickListener {
			// TODO: send email
		}
	}

	override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
			attachments.add(
				Email.Attachment(
					id = "",
					filename = getFilename(data.data!!),
					description = "",
					url = ""
				)
			)

			(root().listAttachments.adapter as? AdapterAttachment)?.update(attachments)
		}
	}

	private fun getFilename(uri: Uri) =
		requireActivity().contentResolver.query(uri, null, null, null, null)?.run {
			moveToFirst()
			val filename = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
			close()
			return@run filename
		}
}
