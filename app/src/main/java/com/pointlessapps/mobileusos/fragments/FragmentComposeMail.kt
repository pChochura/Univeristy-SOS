package com.pointlessapps.mobileusos.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.*
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_attachment.*
import kotlinx.android.synthetic.main.dialog_loading.*
import kotlinx.android.synthetic.main.dialog_message.buttonPrimary
import kotlinx.android.synthetic.main.dialog_message.buttonSecondary
import kotlinx.android.synthetic.main.dialog_message.messageMain
import kotlinx.android.synthetic.main.dialog_message.messageSecondary
import kotlinx.android.synthetic.main.fragment_compose_mail.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.io.File
import java.util.concurrent.CountDownLatch

class FragmentComposeMail(
	private val email: Email? = null,
	private val recipients: MutableList<Email.Recipient> = mutableListOf()
) : FragmentBase() {

	companion object {
		const val PICK_FILE_REQUEST_CODE = 1243
		const val MAX_SIZE = 2e+6
	}

	private val viewModelUser by viewModels<ViewModelUser>()
	private val attachments = mutableListOf<Email.Attachment>()

	override fun getLayoutId() = R.layout.fragment_compose_mail

	override fun created() {
		prepareClickListeners()
		prepareRecipientsList()
		prepareRecipientsEditorListener()
		prepareAttachmentsList()
		prepareData()

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
						onForceGoBack?.invoke()
					}
					dialog.buttonSecondary.setOnClickListener {
						dialog.dismiss()
						saveDraft()
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
						list[position].also {
							recipients.add(it)
							root().listRecipients.addChip(it.name()) {
								recipients.remove(it)
							}
							root().inputRecipients.text.clear()
						}
					}

				isLongClickable = false
			})

			addTextChangedListener {
				if (isPerformingCompletion) {
					return@addTextChangedListener
				}

				(adapter as? AdapterAutocomplete)?.update(
					listOf(
						Email.Recipient(
							it.toString(),
							null
						),
					)
				)
				viewModelUser.getUsersByQuery(it.toString())
					.observe(this@FragmentComposeMail) { (list) ->
						(adapter as? AdapterAutocomplete)?.update(
							listOf(
								Email.Recipient(
									it.toString(),
									null
								),
								*list.map { user ->
									Email.Recipient(
										null,
										user
									)
								}.toTypedArray()
							)
						)
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
				onClickListener = { attachment ->
					DialogUtil.create(requireContext(), R.layout.dialog_attachment, { dialog ->
						dialog.attachmentName.setText(attachment.filename?.withoutExtension())

						dialog.buttonPrimary.setOnClickListener {
							attachment.filename = "%s.%s".format(
								dialog.attachmentName.text.toString(),
								attachment.filename?.extension()
							)
							notifyDataSetChanged()
							dialog.dismiss()
						}
						dialog.buttonSecondary.setOnClickListener {
							if (!attachment.newlyAdded) {
								viewModelUser.deleteEmailAttachment(attachment.id)
							}

							update(attachments.apply { remove(attachment) })
							dialog.dismiss()
						}
					}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
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

			(root().listAttachments.adapter as? AdapterAttachment)?.update(
				(it.attachments ?: listOf())
			)

			viewModelUser.getEmailRecipients(it.id).observe(this) { (recipients) ->
				this.recipients.apply {
					clear()
					addAll(recipients)
				}
				recipients.forEach { recipient ->
					root().listRecipients.addChip(recipient.name()) {
						this.recipients.remove(recipient)
					}
				}
			}
		}
	}

	private fun saveDraft(callback: (Dialog.() -> Unit)? = null) {
		ensureEmailExists { id ->
			saveEmail {
				val loaded =
					CountDownLatch(2 + attachments.filter(Email.Attachment::newlyAdded).size)

				val parts = recipients.partition { it.user !== null }
				viewModelUser.updateEmailRecipients(
					id,
					parts.first.map { it.user!!.id },
					parts.second.map { it.email!! }
				).onFinished {
					if (it != null) {
						dismiss()
						showErrorDialog { dismiss() }
					} else {
						viewModelUser.refreshEmailRecipients(id).onFinished {
							loaded.countDown()
						}
					}
				}

				viewModelUser.updateEmail(
					id,
					root().inputSubject.text.toString(),
					root().inputContent.text.toString()
				).onFinished {
					if (it != null) {
						dismiss()
						showErrorDialog { dismiss() }
					} else {
						loaded.countDown()
					}
				}

				attachments.filter(Email.Attachment::newlyAdded).forEach { attachment ->
					attachment.data?.also {
						getFileData(
							it,
							File.createTempFile("attachment_", attachment.id)
						).also { file ->
							viewModelUser.addEmailAttachment(
								id,
								file.readBytes(),
								attachment.filename!!
							).onOnceCallback { (id) ->
								attachment.id = id ?: return@onOnceCallback
							}.onFinished { error ->
								if (error != null) {
									dismiss()
									showErrorDialog { dismiss() }
								} else {
									loaded.countDown()
								}
							}
						}
					}
				}

				doAsync {
					loaded.await()
					GlobalScope.launch(Dispatchers.Main) {
						if (callback == null) {
							dismiss()
							onForceRefreshAllFragments?.invoke()
							onForceGoBack?.invoke()
						}

						callback?.invoke(this@saveEmail)
					}
				}
			}
		}
	}

	private fun saveEmail(callback: Dialog.() -> Unit) {
		DialogUtil.create(requireContext(), R.layout.dialog_loading, { dialog ->
			dialog.setCancelable(false)

			dialog.messageMain.setText(R.string.saving_email)
			dialog.progressBar.isGone = false
			dialog.buttonPrimary.isGone = true
			dialog.buttonSecondary.isGone = true
			dialog.messageSecondary.isGone = true

			callback(dialog)
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}

	private fun ensureEmailExists(callback: (String) -> Unit) {
		if (email != null) {
			callback(email.id)
			return
		}

		viewModelUser.createEmail(
			root().inputSubject.text.toString(),
			root().inputContent.text.toString()
		).onOnceCallback { (id) ->
			GlobalScope.launch(Dispatchers.Main) { id?.also(callback) }
		}
	}

	private fun prepareClickListeners() {
		root().buttonSend.setOnClickListener {
			saveDraft {
				GlobalScope.launch(Dispatchers.Main) {
					messageMain.setText(R.string.sending_email)
					email?.also { email ->
						if (recipients.size <= 0 || email.content.isNullOrEmpty() || email.subject.isNullOrEmpty()) {
							messageMain.setText(R.string.oops)
							messageSecondary.setText(R.string.sending_email_error_description)
							progressBar.isGone = true
							messageSecondary.isGone = false
							buttonPrimary.isGone = false
							buttonPrimary.setOnClickListener { dismiss() }

							return@launch
						}

						viewModelUser.refreshEmailRecipients(email.id).onFinished {
							viewModelUser.sendEmail(email.id).onFinished {
								dismiss()
								onForceRefreshAllFragments?.invoke()
								onForceGoBack?.invoke()
							}
						}
					}
				}
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data?.data != null) {
			getFileInfo(data.data ?: return)?.also {
				if (it.second.toDouble() > MAX_SIZE) {
					showErrorDialog(R.string.too_large_file) {
						dismiss()
					}
				} else {
					attachments.add(
						Email.Attachment(
							id = "",
							filename = it.first,
							description = "",
							url = "",
							size = it.second?.toLong(),
							data = data.data,
							newlyAdded = true
						)
					)
				}
			}

			(root().listAttachments.adapter as? AdapterAttachment)?.update(attachments)
		}
	}

	private fun getFileInfo(uri: Uri) =
		requireActivity().contentResolver.query(uri, null, null, null, null)?.run {
			moveToFirst()
			val filename = getString(getColumnIndex(OpenableColumns.DISPLAY_NAME))
			val size = getString(getColumnIndex(OpenableColumns.SIZE))
			close()
			return@run filename to size
		}

	private fun getFileData(uri: Uri, file: File) =
		file.apply {
			requireActivity().contentResolver.openInputStream(uri)?.also {
				file.outputStream().use { fileOut ->
					it.copyTo(fileOut)
				}
			}
		}

	private inline fun showErrorDialog(
		message: Int = R.string.something_went_wrong,
		crossinline callback: Dialog.() -> Unit
	) {
		DialogUtil.create(requireContext(), R.layout.dialog_message, { dialog ->
			dialog.messageMain.setText(R.string.oops)
			dialog.messageSecondary.setText(message)
			dialog.buttonSecondary.isGone = true

			dialog.buttonPrimary.setText(android.R.string.ok)
			dialog.buttonPrimary.setOnClickListener { callback(dialog) }
			dialog.setOnCancelListener { callback(dialog) }
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}
}
