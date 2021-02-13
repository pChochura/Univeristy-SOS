package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEmploymentFunction
import com.pointlessapps.mobileusos.adapters.AdapterPhoneNumber
import com.pointlessapps.mobileusos.databinding.FragmentUserBinding
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentUser(private val id: String) :
	FragmentCoreImpl<FragmentUserBinding>(FragmentUserBinding::class.java), FragmentPinnable {

	private val viewModelUser by viewModels<ViewModelUser>()
	private var user: User? = null

	override fun getShortcut(fragment: FragmentCoreImpl<*>, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_profile to fragment.getString(R.string.loading))
		ViewModelProvider(fragment).get(ViewModelUser::class.java).getUserById(id)
			.onOnceCallback { (user) ->
				if (user !== null) {
					GlobalScope.launch(Dispatchers.Main) {
						callback(R.drawable.ic_profile to user.name(false))
					}

					return@onOnceCallback
				}
			}
	}

	override fun created() {
		refreshed()
		preparePhoneNumbersList()
		prepareEmploymentFunctionsList()
		prepareClickListeners()

		setCollapsible(binding().buttonOfficeHours, binding().userOfficeHours)
		setCollapsible(binding().buttonInterests, binding().userInterests)

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		if (isPinned(javaClass.name, id)) {
			binding().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		binding().horizontalProgressBar.isRefreshing = true
		prepareData {
			binding().pullRefresh.isRefreshing = false
			binding().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		binding().buttonEmail.setOnClickListener {
			onChangeFragment?.invoke(
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

		binding().buttonRoom.setOnClickListener {
			onChangeFragment?.invoke(
				FragmentRoom(user?.room?.id ?: return@setOnClickListener)
			)
		}

		binding().buttonPin.setOnClickListener {
			binding().buttonPin.setIconResource(
				if (togglePin(javaClass.name, id))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		viewModelUser.getUserById(id).observe(this) { (user) ->
			if (user == null) {
				return@observe
			}
			this.user = user

			(binding().listEmploymentFunctions.adapter as? AdapterEmploymentFunction)?.update(
				user.employmentFunctions ?: listOf()
			)
			binding().listEmploymentFunctions.setEmptyText(getString(R.string.no_employment_functions))

			(binding().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
				user.phoneNumbers?.map(Building::PhoneNumber) ?: listOf()
			)
			binding().listPhoneNumbers.setEmptyText(getString(R.string.no_phone_numbers))

			binding().userEmail.text = user.email ?: let {
				binding().buttonEmail.isClickable = false
				requireContext().getString(R.string.no_email_address)
			}
			binding().userOfficeHours.text = user.officeHours?.toString()
			binding().userInterests.text = user.interests?.toString()
			binding().userRoom.text = user.room?.number
			binding().userBuilding.text = user.room?.building?.name?.toString()
			binding().userName.text = user.name()

			Picasso.get().load(user.photoUrls?.values?.firstOrNull() ?: return@observe)
				.into(binding().userProfileImg)

			hideEmptyElements()
		}.onFinished { callback?.invoke() }
	}

	private fun hideEmptyElements() {
		if (user?.room?.number.isNullOrEmpty()) {
			binding().buttonRoom.isVisible = false
			binding().labelRoom.isVisible = false
		}

		if (user?.officeHours?.toString().isNullOrBlank()) {
			binding().buttonOfficeHours.isVisible = false
		}

		if (user?.interests?.toString().isNullOrBlank()) {
			binding().buttonInterests.isVisible = false
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
				TransitionManager.beginDelayedTransition(binding().root, AutoTransition())
			}
		}
	}

	private fun prepareEmploymentFunctionsList() {
		binding().listEmploymentFunctions.apply {
			setAdapter(AdapterEmploymentFunction())
		}
	}

	private fun preparePhoneNumbersList() {
		binding().listPhoneNumbers.apply {
			setAdapter(AdapterPhoneNumber().apply {
				onClickListener = {
					Utils.phoneIntent(requireContext(), it.number)
				}
			})
		}
	}
}
