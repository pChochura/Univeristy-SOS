package com.pointlessapps.mobileusos.fragments

import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterPhoneNumber
import com.pointlessapps.mobileusos.adapters.AdapterRoom
import com.pointlessapps.mobileusos.databinding.FragmentBuildingBinding
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Keep
class FragmentBuilding(private val id: String) :
	FragmentCoreImpl<FragmentBuildingBinding>(FragmentBuildingBinding::class.java),
	FragmentPinnable {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun getShortcut(fragment: FragmentCoreImpl<*>, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_building to fragment.getString(R.string.loading))
		ViewModelProvider(fragment).get(ViewModelCommon::class.java).getBuildingById(id)
			.onOnceCallback { (building) ->
				if (building !== null) {
					GlobalScope.launch(Dispatchers.Main) {
						callback(R.drawable.ic_building to building.name.toString())
					}

					return@onOnceCallback
				}
			}
	}

	override fun created() {
		preparePhonesList()
		prepareRoomsList()
		prepareClickListeners()
		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		if (isPinned(javaClass.name, id)) {
			binding().buttonPin.setIconResource(R.drawable.ic_unpin)
		}

		binding().horizontalProgressBar.isRefreshing = true
		prepareData {
			binding().horizontalProgressBar.isRefreshing = false
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		viewModelCommon.getBuildingById(id).observe(this) { (building) ->
			if (building == null) {
				return@observe
			}

			prepareDataStatic(building)

			(binding().listRooms.adapter as? AdapterRoom)?.update(building.rooms ?: return@observe)
			(binding().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
				building.allPhoneNumbers ?: return@observe
			)
		}.onFinished { callback?.invoke() }
	}

	private fun prepareClickListeners() {
		binding().buttonPin.setOnClickListener {
			binding().buttonPin.setIconResource(
				if (togglePin(javaClass.name, id))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun prepareDataStatic(building: Building) {
		binding().buildingName.text = building.name?.toString()
		binding().campusName.text = building.campusName?.toString()
		building.staticMapUrls?.values?.firstOrNull()?.also { map ->
			binding().containerBuildingLocation.isVisible = true
			Picasso.get().load(map).into(binding().buildingMap)
			binding().buildingMap.setOnClickListener {
				Utils.mapsIntent(
					requireContext(),
					building.location?.lat,
					building.location?.long,
					building.name?.toString()
				)
			}
		}
	}

	private fun preparePhonesList() {
		binding().listPhoneNumbers.apply {
			setAdapter(AdapterPhoneNumber().apply {
				onClickListener = { Utils.phoneIntent(requireContext(), it.number) }
			})

			setEmptyText(getString(R.string.no_phone_numbers))
		}
	}

	private fun prepareRoomsList() {
		binding().listRooms.apply {
			setAdapter(AdapterRoom().apply {
				onClickListener = {
					onChangeFragment?.invoke(FragmentRoom(it.id))
				}
			})

			setEmptyText(getString(R.string.no_rooms))
		}
	}
}
