package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.chip.Chip
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_room.view.*
import org.jetbrains.anko.find

class FragmentRoom(private val roomName: String?, private val roomId: String) : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_room

	override fun created() {
		roomName?.also { root().roomName.text = it }

		prepareData()
		prepareMeetingsList()
	}

	private fun prepareData() {
		viewModelCommon.getRoomById(roomId).observe(this) { room ->
			root().roomName.text = room.number.toString()

			room.building?.name?.toString()?.also { buildingName ->
				root().buildingName.text = buildingName
			}

			room.building?.staticMapUrls?.values?.first()?.also { map ->
				Picasso.get().load(map).into(root().buildingMap)
				root().buildingMap.setOnClickListener {
					val gmmIntentUri =
						Uri.parse(
							"geo:${room.building?.location?.lat},${room.building?.location?.long}?q=${Uri.encode(
								room.building?.name?.toString()
							)}"
						)
					val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
					mapIntent.setPackage("com.google.android.apps.maps")
					mapIntent.resolveActivity(activity?.packageManager ?: return@setOnClickListener)
						?.let {
							startActivity(mapIntent)
						}
				}
			}

			root().roomAttributes.removeAllViews()

			room.capacity?.also {
				root().roomAttributes.addView((LayoutInflater.from(requireContext())
					.inflate(R.layout.list_item_room_attribute, null) as ViewGroup).apply {
					find<AppCompatTextView>(R.id.attributeName).text = getString(R.string.capacity)
					find<Chip>(R.id.attributeValue).text = room.capacity.toString()
					setPadding(0)
				})
			}

			room.attributes?.forEach { attribute ->
				root().roomAttributes.addView((LayoutInflater.from(requireContext())
					.inflate(R.layout.list_item_room_attribute, null) as ViewGroup).apply {
					find<AppCompatTextView>(R.id.attributeName).text =
						attribute.description.toString()
					find<Chip>(R.id.attributeValue).text =
						(attribute.count ?: getString(R.string.available)).toString()
				})
			}
		}
	}

	private fun prepareMeetingsList() {
		root().listMeetings.setAdapter(AdapterMeeting(true).apply {
			onAddToCalendarClickListener = { Utils.calendarIntent(requireContext(), it) }
		})
		root().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))

		viewModelTimetable.getByRoomId(roomId).observe(this) {
			(root().listMeetings.adapter as? AdapterMeeting)?.update(it)
		}
	}
}
