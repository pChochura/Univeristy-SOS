package com.pointlessapps.mobileusos.fragments

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FragmentRoom(private val id: String) : FragmentBase(), FragmentPinnable {

	private val viewModelCommon by viewModels<ViewModelCommon>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	override fun getLayoutId() = R.layout.fragment_room

	override fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_room to fragment.getString(R.string.loading))
		ViewModelProvider(fragment).get(ViewModelCommon::class.java).getRoomById(id)
			.onOnceCallback { (room) ->
				if (room !== null) {
					GlobalScope.launch(Dispatchers.Main) {
						callback(R.drawable.ic_room to room.number.toString())
					}

					return@onOnceCallback
				}
			}
	}

	override fun created() {
		refreshed()
		prepareMeetingsList()
		prepareAttributesList()
		prepareClickListeners()

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

	private fun prepareData(callback: (() -> Unit)? = null) {
		viewModelCommon.getRoomById(id).observe(this) { (room) ->
			if (room === null) {
				return@observe
			}

			root().roomName.text = room.number.toString()

			room.building?.name?.toString()?.also { buildingName ->
				root().buttonBuilding.text = buildingName

				root().buttonBuilding.setOnClickListener {
					onChangeFragment?.invoke(
						FragmentBuilding(
							room.building?.id ?: return@setOnClickListener
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
		}.onFinished { callback?.invoke() }
	}

	private fun prepareClickListeners() {
		root().buttonPin.setOnClickListener {
			root().buttonPin.setIconResource(
				if (togglePin(javaClass.name, id))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
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

		viewModelTimetable.getByRoomId(id).observe(this) { (list) ->
			(root().listMeetings.adapter as? AdapterMeeting)?.update(list)
		}
	}
}
