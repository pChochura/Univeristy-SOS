package com.pointlessapps.mobileusos.fragments

import androidx.annotation.Keep
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterAttributes
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.databinding.FragmentRoomBinding
import com.pointlessapps.mobileusos.models.BuildingRoom
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.util.concurrent.CountDownLatch

@Keep
class FragmentRoom(private val id: String) :
	FragmentCoreImpl<FragmentRoomBinding>(FragmentRoomBinding::class.java), FragmentPinnable {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelCommon by viewModels<ViewModelCommon>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()

	private var room: BuildingRoom? = null

	override fun getShortcut(fragment: FragmentCoreImpl<*>, callback: (Pair<Int, String>) -> Unit) {
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

	private fun prepareData(callback: (() -> Unit)? = null) {
		val loaded = CountDownLatch(2)

		viewModelCommon.getRoomById(id).observe(this) { (room) ->
			if (room === null) {
				return@observe
			}

			this.room = room
			binding().roomName.text = room.number.toString()

			room.building?.name?.toString()?.also { buildingName ->
				binding().buttonBuilding.text = buildingName

				binding().buttonBuilding.setOnClickListener {
					onChangeFragment?.invoke(
						FragmentBuilding(
							room.building?.id ?: return@setOnClickListener
						)
					)
				}
			}

			room.building?.staticMapUrls?.values?.firstOrNull()?.also { map ->
				binding().containerBuildingLocation.isVisible = true
				Picasso.get().load(map).into(binding().buildingMap)
				binding().buildingMap.setOnClickListener {
					Utils.mapsIntent(
						requireContext(),
						room.building?.location?.lat,
						room.building?.location?.long,
						room.building?.name?.toString()
					)
				}
			}

			(binding().listAttributes.adapter as? AdapterAttributes)?.update(
				listOf(
					BuildingRoom.Attribute(
						"capacity",
						Name(getString(R.string.capacity)),
						room.capacity
					),
					*(room.attributes ?: listOf()).toTypedArray()
				)
			)
		}.onFinished { loaded.countDown() }

		viewModelTimetable.getByRoomId(id).observe(this) { (list) ->
			(binding().listMeetings.adapter as? AdapterMeeting)?.update(list)
		}.onFinished { loaded.countDown() }

		doAsync {
			loaded.await()
			callback?.invoke()
		}
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

	private fun prepareAttributesList() {
		binding().listAttributes.apply {
			adapter = AdapterAttributes()
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareMeetingsList() {
		binding().listMeetings.setAdapter(AdapterMeeting(true).apply {
			onClickListener = {
				Utils.showCourseInfo(requireContext(), it.apply {
					roomNumber = room?.number
					buildingName = room?.building?.name
				}, viewModelUser, onChangeFragment)
			}
		})
		binding().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
	}
}
