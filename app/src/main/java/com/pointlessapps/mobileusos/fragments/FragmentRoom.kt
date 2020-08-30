package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttributes
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_room.view.*

class FragmentRoom(private val roomName: String?, private val roomId: String) : FragmentBase() {

	private val viewModelCommon by viewModels<ViewModelCommon>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_room

	override fun created() {
		roomName?.also { root().roomName.text = it }

		refreshed()
		prepareMeetingsList()
		prepareAttributesList()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		root().horizontalProgressBar.isRefreshing = true
		prepareData {
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		viewModelCommon.getRoomById(roomId).observe(this) { (room, online) ->
			root().roomName.text = room.number.toString()

			room.building?.name?.toString()?.also { buildingName ->
				root().buttonBuilding.text = buildingName

				root().buttonBuilding.setOnClickListener {
					onChangeFragmentListener?.invoke(
						FragmentBuilding(
							room.building ?: return@setOnClickListener
						)
					)
				}
			}

			room.building?.staticMapUrls?.values?.first()?.also { map ->
				Picasso.get().load(map).into(root().buildingMap)
				root().buildingMap.setOnClickListener {
					Utils.mapsIntent(
						requireContext(),
						room.building?.location?.lat,
						room.building?.location?.long,
						room.building?.name?.toString()
					)
				}
			}

			(root().listAttributes.adapter as? AdapterAttributes)?.update(
				listOf(
					BuildingRoom.Attribute(
						"capacity",
						Name(getString(R.string.capacity)),
						room.capacity
					),
					*(room.attributes ?: listOf()).toTypedArray()
				)
			)

			if (online) {
				callback?.invoke()
			}
		}
	}

	private fun prepareAttributesList() {
		root().listAttributes.apply {
			adapter = AdapterAttributes()
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
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
