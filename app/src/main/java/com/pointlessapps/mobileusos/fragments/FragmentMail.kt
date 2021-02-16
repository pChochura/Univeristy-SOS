package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.databinding.DialogMessageBinding
import com.pointlessapps.mobileusos.databinding.FragmentMailBinding
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentMail(private var email: Email) :
	FragmentCoreImpl<FragmentMailBinding>(FragmentMailBinding::class.java) {

	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun created() {
		prepareAttachmentsList()
		prepareClickListeners()
		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		binding().emailSubject.text = email.subject

		val loaded = CountDownLatch(2)
		binding().horizontalProgressBar.isRefreshing = true
		binding().pullRefresh.isRefreshing = true

		viewModelUser.getEmailById(email.id).observe(this) { (email) ->
			if (email != null) {
				this.email = email
			}

			binding().emailSubject.text = email?.subject
			binding().emailContent.text = Utils.parseHtml(email?.content ?: "")
			binding().emailContent.movementMethod = LinkMovementMethod.getInstance()

			email?.attachments?.also { list ->
				(binding().listAttachments.adapter as? AdapterAttachment)?.update(list)
			}

			if (email?.attachments?.isEmpty() == true) {
				binding().labelAttachments.visibility = View.GONE
				binding().listAttachments.visibility = View.GONE
				binding().divider.visibility = View.GONE
			}

			if (email?.status == "draft") {
				binding().buttonEdit.visibility = View.VISIBLE
			}
		}.onFinished { loaded.countDown() }

		viewModelUser.getEmailRecipients(email.id).observe(this) { (list) ->
			binding().emailRecipient.text =
				list.joinToString { item -> item.name() }.takeIf(String::isNotBlank)
					?: getString(R.string.no_recipient)
			binding().emailDate.text = dateFormat.format(email.date ?: Date())

			if (list.size == 1) {
				Picasso.get()
					.load(
						list.firstOrNull()?.user?.photoUrls?.values?.firstOrNull() ?: return@observe
					)
					.into(binding().emailRecipientImg)

				binding().emailRecipientImg.setColorFilter(Color.TRANSPARENT)
			}
		}.onFinished { loaded.countDown() }

		doAsync {
			loaded.await()

			GlobalScope.launch(Dispatchers.Main) {
				binding().pullRefresh.isRefreshing = false
				binding().horizontalProgressBar.isRefreshing = false
			}
		}
	}

	private fun prepareAttachmentsList() {
		binding().listAttachments.setAdapter(AdapterAttachment().apply {
			onClickListener = {
				startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(it.url)))
			}
		})
	}

	private fun prepareClickListeners() {
		binding().buttonEdit.setOnClickListener {
			onChangeFragment?.invoke(FragmentComposeMail(email))
		}

		binding().buttonDelete.setOnClickListener {
			deleteEmail()
		}
	}

	private fun deleteEmail() {
		DialogUtil.create(requireContext(), DialogMessageBinding::class.java, { dialog ->
			dialog.messageMain.setText(R.string.are_you_sure)
			dialog.messageSecondary.setText(R.string.delete_email_description)

			dialog.buttonPrimary.setText(R.string.confirm)
			dialog.buttonPrimary.setOnClickListener {
				viewModelUser.deleteEmail(email.id).onFinished {
					dismiss()
					onForceGoBack?.invoke()
				}
			}
			dialog.buttonSecondary.setText(R.string.cancel)
			dialog.buttonSecondary.setOnClickListener { dismiss() }
		})
	}
}
