package com.pointlessapps.mobileusos.fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.view.KeyEvent
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.databinding.DialogAttachmentBinding
import com.pointlessapps.mobileusos.databinding.DialogLoadingBinding
import com.pointlessapps.mobileusos.databinding.DialogMessageBinding
import com.pointlessapps.mobileusos.databinding.FragmentComposeMailBinding
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getScopeMailClient
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.*
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.io.File
import java.util.concurrent.CountDownLatch

class FragmentComposeMail(
	private val email: Email? = null,
	private val recipients: MutableList<Email.Recipient> = mutableListOf()
) : FragmentCoreImpl<FragmentComposeMailBinding>(FragmentComposeMailBinding::class.java) {

	companion object {
		const val MAX_SIZE = 2e+6
	}

	private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
	private val viewModelUser by viewModels<ViewModelUser>()
	private val attachments = mutableListOf<Email.Attachment>()

	override fun created() {
		checkScopes() ?: return

		prepareClickListeners()
		prepareRecipientsList()
		prepareRecipientsEditorListener()
		prepareAttachmentsList()
		prepareData()

		activityResultLauncher =
			registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
				getFileInfo(result.data?.data ?: return@registerForActivityResult)?.also {
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
								data = result.data?.data,
								newlyAdded = true
							)
						)
					}
				}

				(binding().listAttachments.adapter as? AdapterAttachment)?.update(
					attachments
				)
			}

		onBackPressedListener = {
			DialogUtil.create(
				requireContext(),
				DialogMessageBinding::class.java,
				{ dialog ->
					dialog.messageMain.text = getString(R.string.discard_email_title)
					dialog.messageSecondary.text = getString(R.string.discard_email_message)
					dialog.buttonPrimary.text = getString(R.string.discard)
					dialog.buttonSecondary.text = getString(R.string.save_draft)

					dialog.buttonPrimary.setOnClickListener {
						dismiss()
						onForceGoBack?.invoke()
					}
					dialog.buttonSecondary.setOnClickListener {
						dismiss()
						saveDraft()
					}
				}
			)
			true
		}
	}

	override fun refreshed() {
		checkScopes() ?: return
	}

	private fun checkScopes(): Boolean? {
		if (!Preferences.get().getScopeMailClient()) {
			Utils.askForRelogin(requireActivity(), R.string.scopes_missing_description) {
				onForceGoBack?.invoke()
			}

			return null
		}

		return true
	}

	private fun prepareRecipientsEditorListener() {
		binding().inputRecipients.setOnKeyListener { _, keyCode, keyEvent ->
			if (keyEvent.action == KeyEvent.ACTION_DOWN) {
				when (keyCode) {
					KeyEvent.KEYCODE_DEL -> {
						if (binding().inputRecipients.text.isNullOrEmpty() && recipients.isNotEmpty()) {
							recipients.removeAt(recipients.lastIndex)
							binding().listRecipients.removeViewAt(binding().listRecipients.childCount - 2)

							return@setOnKeyListener true
						}
					}
					KeyEvent.KEYCODE_ENTER -> {
						val recipient =
							Email.Recipient(binding().inputRecipients.text.toString(), null)
						recipients.add(recipient)
						binding().listRecipients.addChip(recipient.email!!) {
							recipients.remove(recipient)
						}

						binding().inputRecipients.text.clear()
						return@setOnKeyListener true
					}
				}
			}

			return@setOnKeyListener false
		}
	}

	private fun prepareRecipientsList() {
		binding().inputRecipients.apply {
			setAdapter(AdapterAutocomplete(requireContext()).apply {
				onItemClickListener =
					AdapterView.OnItemClickListener { _, _, position, _ ->
						list[position].also {
							recipients.add(it)
							binding().listRecipients.addChip(it.name()) {
								recipients.remove(it)
							}
							binding().inputRecipients.text.clear()
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
			binding().listRecipients.addChip(it.name()) { recipients.remove(it) }
		}
	}

	private fun prepareAttachmentsList() {
		binding().listAttachments.apply {
			adapter = AdapterAttachment(true).apply {
				onClickListener = { attachment ->
					DialogUtil.create(
						requireContext(),
						DialogAttachmentBinding::class.java,
						{ dialog ->
							dialog.attachmentName.setText(attachment.filename?.withoutExtension())

							dialog.buttonPrimary.setOnClickListener {
								attachment.filename = "%s.%s".format(
									dialog.attachmentName.text.toString(),
									attachment.filename?.extension()
								)
								notifyDataSetChanged()
								dismiss()
							}
							dialog.buttonSecondary.setOnClickListener {
								if (!attachment.newlyAdded) {
									viewModelUser.deleteEmailAttachment(attachment.id)
								}

								update(attachments.apply { remove(attachment) })
								dismiss()
							}
						}
					)
				}
				onAddClickListener = {
					val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
					intent.type = "*/*"
					intent.addCategory(Intent.CATEGORY_DEFAULT)
					activityResultLauncher.launch(
						Intent.createChooser(
							intent,
							getString(R.string.choose_an_attachment)
						)
					)
				}
			}
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareData() {
		email?.also {
			binding().inputSubject.setText(it.subject ?: "")
			binding().inputContent.setText(it.content ?: "")

			(binding().listAttachments.adapter as? AdapterAttachment)?.update(
				(it.attachments ?: listOf())
			)

			viewModelUser.getEmailRecipients(it.id).observe(this) { (recipients) ->
				this.recipients.apply {
					clear()
					addAll(recipients)
				}
				recipients.forEach { recipient ->
					binding().listRecipients.addChip(recipient.name()) {
						this.recipients.remove(recipient)
					}
				}
			}
		}
	}

	private fun saveDraft(callback: (DialogLoadingBinding.(Dialog) -> Unit)? = null) {
		ensureEmailExists { id ->
			saveEmail { dialog ->
				val loaded =
					CountDownLatch(2 + attachments.filter(Email.Attachment::newlyAdded).size)

				val parts = recipients.partition { it.user !== null }
				viewModelUser.updateEmailRecipients(
					id,
					parts.first.map { it.user!!.id },
					parts.second.map { it.email!! }
				).onFinished {
					if (it != null) {
						dialog.dismiss()
						showErrorDialog { dismiss() }
					} else {
						viewModelUser.refreshEmailRecipients(id).onFinished {
							loaded.countDown()
						}
					}
				}

				viewModelUser.updateEmail(
					id,
					binding().inputSubject.text.toString(),
					binding().inputContent.text.toString()
				).onFinished {
					if (it != null) {
						dialog.dismiss()
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
									dialog.dismiss()
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
							dialog.dismiss()
							onForceRefreshAllFragments?.invoke()
							onForceGoBack?.invoke()
						}

						callback?.invoke(this@saveEmail, dialog)
					}
				}
			}
		}
	}

	private fun saveEmail(callback: DialogLoadingBinding.(Dialog) -> Unit) {
		DialogUtil.create(requireContext(), DialogLoadingBinding::class.java, { dialog ->
			setCancelable(false)

			dialog.messageMain.setText(R.string.saving_email)
			dialog.progressBar.isGone = false
			dialog.buttonPrimary.isGone = true
			dialog.buttonSecondary.isGone = true
			dialog.messageSecondary.isGone = true

			callback(dialog, this)
		})
	}

	private fun ensureEmailExists(callback: (String) -> Unit) {
		if (email != null) {
			callback(email.id)
			return
		}

		viewModelUser.createEmail(
			binding().inputSubject.text.toString(),
			binding().inputContent.text.toString()
		).onOnceCallback { (id) ->
			GlobalScope.launch(Dispatchers.Main) { id?.also(callback) }
		}
	}

	private fun prepareClickListeners() {
		binding().buttonSend.setOnClickListener {
			saveDraft { dialog ->
				GlobalScope.launch(Dispatchers.Main) {
					messageMain.setText(R.string.sending_email)
					email?.also { email ->
						if (recipients.size <= 0 || email.content.isNullOrEmpty() || email.subject.isNullOrEmpty()) {
							messageMain.setText(R.string.oops)
							messageSecondary.setText(R.string.sending_email_error_description)
							progressBar.isGone = true
							messageSecondary.isGone = false
							buttonPrimary.isGone = false
							buttonPrimary.setOnClickListener { dialog.dismiss() }

							return@launch
						}

						viewModelUser.refreshEmailRecipients(email.id).onFinished {
							viewModelUser.sendEmail(email.id).onFinished {
								dialog.dismiss()
								onForceRefreshAllFragments?.invoke()
								onForceGoBack?.invoke()
							}
						}
					}
				}
			}
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
		DialogUtil.create(requireContext(), DialogMessageBinding::class.java, { dialog ->
			dialog.messageMain.setText(R.string.oops)
			dialog.messageSecondary.setText(message)
			dialog.buttonSecondary.isGone = true

			dialog.buttonPrimary.setText(android.R.string.ok)
			dialog.buttonPrimary.setOnClickListener { callback(this) }
			setOnCancelListener { callback(this) }
		})
	}
}
