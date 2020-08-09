package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEmploymentFunction
import com.pointlessapps.mobileusos.adapters.AdapterPhoneNumber
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.view.*

class FragmentUser(private val userId: String) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private var user: User? = null

	override fun getLayoutId() = R.layout.fragment_user

	override fun created() {
		prepareData()
		preparePhoneNumbersList()
		prepareEmploymentFunctionsList()
		prepareClickListeners()

		setCollapsible(root().buttonOfficeHours, root().userOfficeHours)
		setCollapsible(root().buttonInterests, root().userInterests)
	}

	private fun prepareClickListeners() {
		root().buttonEmail.setOnClickListener {
			onChangeFragmentListener?.invoke(
				FragmentComposeMail(
					recipients = mutableListOf(
						Email.Recipient(
							user?.email,
							user
						)
					)
				)
			)
		}

		root().buttonRoom.setOnClickListener {
			onChangeFragmentListener?.invoke(
				FragmentRoom(
					user?.room?.number,
					user?.room?.id ?: return@setOnClickListener
				)
			)
		}
	}

	private fun prepareData() {
		viewModelUser.getUserById(userId).observe(this) {
			if (it == null) {
				return@observe
			}
			user = it

			(root().listEmploymentFunctions.adapter as? AdapterEmploymentFunction)?.update(
				it.employmentFunctions ?: listOf()
			)
			root().listEmploymentFunctions.setEmptyText(getString(R.string.no_employment_functions))

			(root().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
				it.phoneNumbers?.map { number ->
					Building.PhoneNumber(
						number
					)
				} ?: listOf()
			)
			root().listPhoneNumbers.setEmptyText(getString(R.string.no_phone_numbers))

			root().userEmail.text = it.email
			root().userOfficeHours.text = it.officeHours?.toString()
			root().userInterests.text = it.interests?.toString()
			root().userRoom.text = it.room?.number
			root().userBuilding.text = it.room?.building?.name?.toString()
			root().userName.text = it.name()

			Picasso.get().load(it.photoUrls?.values?.firstOrNull() ?: return@observe)
				.into(root().userProfileImg)

			hideEmptyElements()
		}
	}

	private fun hideEmptyElements() {
		if (user?.room?.number.isNullOrEmpty()) {
			root().buttonRoom.isVisible = false
			root().labelRoom.isVisible = false
		}

		if (user?.officeHours?.toString().isNullOrBlank()) {
			root().buttonOfficeHours.isVisible = false
		}

		if (user?.interests?.toString().isNullOrBlank()) {
			root().buttonInterests.isVisible = false
		}
	}

	private fun setCollapsible(button: MaterialButton, view: AppCompatTextView) {
		button.setOnClickListener {
			view.isVisible.also {
				view.isVisible = !it
				button.setIconResource(
					if (it) {
						R.drawable.ic_arrow_down
					} else {
						R.drawable.ic_arrow_up
					}
				)
				TransitionManager.beginDelayedTransition(root(), AutoTransition())
			}
		}
	}

	private fun prepareEmploymentFunctionsList() {
		root().listEmploymentFunctions.apply {
			setAdapter(AdapterEmploymentFunction())
		}
	}

	private fun preparePhoneNumbersList() {
		root().listPhoneNumbers.apply {
			setAdapter(AdapterPhoneNumber().apply {
				onClickListener = {
					Utils.phoneIntent(requireContext(), it.number)
				}
			})
		}
	}
}
