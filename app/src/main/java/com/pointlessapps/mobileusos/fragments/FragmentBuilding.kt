package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterPhoneNumber
import com.pointlessapps.mobileusos.adapters.AdapterRoom
import com.pointlessapps.mobileusos.models.Building
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_building.view.*

class FragmentBuilding(private var building: Building) : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun getLayoutId() = R.layout.fragment_building

	override fun created() {
		preparePhonesList()
		prepareRoomsList()
		prepareData()

		viewModelCommon.getBuildingById(building.id).observe(this) {
			building = it ?: return@observe
			prepareData()

			(root().listRooms.adapter as? AdapterRoom)?.update(it.rooms ?: return@observe)
			(root().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
				it.allPhoneNumbers ?: return@observe
			)
		}
	}

	private fun prepareData() {
		root().buildingName.text = building.name?.toString()
		root().campusName.text = building.campusName?.toString()
		building.staticMapUrls?.values?.first()?.also { map ->
			Picasso.get().load(map).into(root().buildingMap)
			root().buildingMap.setOnClickListener {
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
		root().listPhoneNumbers.apply {
			setAdapter(AdapterPhoneNumber().apply {
				onClickListener = { Utils.phoneIntent(requireContext(), it.number) }
			})

			setEmptyText(getString(R.string.no_phone_numbers))
		}

		(root().listPhoneNumbers.adapter as? AdapterPhoneNumber)?.update(
			building.phoneNumbers?.map { Building.PhoneNumber(it) } ?: return
		)
	}

	private fun prepareRoomsList() {
		root().listRooms.apply {
			setAdapter(AdapterRoom().apply {
				onClickListener = {
					onChangeFragmentListener?.invoke(FragmentRoom(it.number, it.id))
				}
			})

			setEmptyText(getString(R.string.no_rooms))
		}
	}
}