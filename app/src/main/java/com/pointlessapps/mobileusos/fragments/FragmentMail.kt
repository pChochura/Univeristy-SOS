package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.viewModels
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttachment
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_mail.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentMail(private var email: Email) : FragmentBase() {

	private val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_mail

	override fun created() {
		prepareData()
		prepareAttachmentsList()
		prepareClickListeners()
	}

	private fun prepareData() {
		root().emailSubject.text = email.subject

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
		}

		viewModelUser.getEmailRecipients(email.id).observe(this) { (list) ->
			root().emailRecipient.text =
				list.joinToString { item -> item.name() }.takeIf(String::isNotBlank)
					?: getString(R.string.no_recipient)
			root().emailDate.text = dateFormat.format(email.date ?: Date())

			if (list.size == 1) {
				Picasso.get()
					.load(list.firstOrNull()?.user?.photoUrls?.values?.first() ?: return@observe)
					.into(root().emailRecipientImg)

				root().emailRecipientImg.setColorFilter(Color.TRANSPARENT)
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
			onChangeFragmentListener?.invoke(FragmentComposeMail(email))
		}
	}
}
