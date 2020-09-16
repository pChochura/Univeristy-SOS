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
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.models.Email
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_user.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentUser(private val id: String) : FragmentBase(), FragmentPinnable {

	private val viewModelUser by viewModels<ViewModelUser>()
	private var user: User? = null

	override fun getLayoutId() = R.layout.fragment_user

	override fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) {
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

		setCollapsible(root().buttonOfficeHours, root().userOfficeHours)
		setCollapsible(root().buttonInterests, root().userInterests)

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		if (isPinned(javaClass.name, id)) {
			root().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		root().horizontalProgressBar.isRefreshing = true
		prepareData {
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		root().buttonEmail.setOnClickListener {
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

		root().buttonRoom.setOnClickListener {
			onChangeFragment?.invoke(
				FragmentRoom(user?.room?.id ?: return@setOnClickListener)
			)
		}

		root().buttonPin.setOnClickListener {
			root().buttonPin.setIconResource(
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

			(root().listEmploymentFunctions.adapter as? AdapterEmploymentFunction)?.update(
				user.employmentFunctions ?: listOf()
			)
			root().listEmploymentFunctions.setEmptyText(getString(R.string.no_employment_functions))

			(root().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
				user.phoneNumbers?.map(Building::PhoneNumber) ?: listOf()
			)
			root().listPhoneNumbers.setEmptyText(getString(R.string.no_phone_numbers))

			root().userEmail.text = user.email
			root().userOfficeHours.text = user.officeHours?.toString()
			root().userInterests.text = user.interests?.toString()
			root().userRoom.text = user.room?.number
			root().userBuilding.text = user.room?.building?.name?.toString()
			root().userName.text = user.name()

			Picasso.get().load(user.photoUrls?.values?.firstOrNull() ?: return@observe)
				.into(root().userProfileImg)

			hideEmptyElements()
		}.onFinished { callback?.invoke() }
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
