package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.fragment_mail.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentMail(private var email: Email) : FragmentBase() {

	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_mail

	override fun created() {
		prepareAttachmentsList()
		prepareClickListeners()
		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		root().emailSubject.text = email.subject

		val loaded = CountDownLatch(2)
		root().horizontalProgressBar.isRefreshing = true
		root().pullRefresh.isRefreshing = true

		viewModelUser.getEmailById(email.id).observe(this) { (email) ->
			if (email != null) {
				this.email = email
			}

			root().emailSubject.text = email?.subject
			root().emailContent.text = Utils.parseHtml(email?.content ?: "")
			root().emailContent.movementMethod = LinkMovementMethod.getInstance()

			email?.attachments?.also { list ->
				(root().listAttachments.adapter as? AdapterAttachment)?.update(list)
			}

			if (email?.attachments?.isEmpty() == true) {
				root().labelAttachments.visibility = View.GONE
				root().listAttachments.visibility = View.GONE
				root().divider.visibility = View.GONE
			}

			if (email?.status == "draft") {
				root().buttonEdit.visibility = View.VISIBLE
			}
		}.onFinished { loaded.countDown() }

		viewModelUser.getEmailRecipients(email.id).observe(this) { (list) ->
			root().emailRecipient.text =
				list.joinToString { item -> item.name() }.takeIf(String::isNotBlank)
					?: getString(R.string.no_recipient)
			root().emailDate.text = dateFormat.format(email.date ?: Date())

			if (list.size == 1) {
				Picasso.get()
					.load(
						list.firstOrNull()?.user?.photoUrls?.values?.firstOrNull() ?: return@observe
					)
					.into(root().emailRecipientImg)

				root().emailRecipientImg.setColorFilter(Color.TRANSPARENT)
			}
		}.onFinished { loaded.countDown() }

		doAsync {
			loaded.await()

			GlobalScope.launch(Dispatchers.Main) {
				root().pullRefresh.isRefreshing = false
				root().horizontalProgressBar.isRefreshing = false
			}
		}
	}

	private fun prepareAttachmentsList() {
		root().listAttachments.setAdapter(AdapterAttachment().apply {
			onClickListener = {
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
			}
		})
	}

	private fun prepareClickListeners() {
		root().buttonEdit.setOnClickListener {
			onChangeFragment?.invoke(FragmentComposeMail(email))
		}

		root().buttonDelete.setOnClickListener {
			deleteEmail()
		}
	}

	private fun deleteEmail() {
		DialogUtil.create(requireContext(), R.layout.dialog_message, { dialog ->
			dialog.messageMain.setText(R.string.are_you_sure)
			dialog.messageSecondary.setText(R.string.delete_email_description)

			dialog.buttonPrimary.setText(R.string.confirm)
			dialog.buttonPrimary.setOnClickListener {
				viewModelUser.deleteEmail(email.id).onFinished {
					dialog.dismiss()
					onForceGoBack?.invoke()
				}
			}
			dialog.buttonSecondary.setText(R.string.cancel)
			dialog.buttonSecondary.setOnClickListener { dialog.dismiss() }
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}
}
