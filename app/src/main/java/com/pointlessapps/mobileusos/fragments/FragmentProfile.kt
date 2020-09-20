package com.pointlessapps.mobileusos.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterRecentGrade
import com.pointlessapps.mobileusos.adapters.AdapterTerm
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getProfileShortcuts
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.list_item_shortcut.view.*
import kotlinx.android.synthetic.main.partial_profile_shortcuts.view.*
import org.jetbrains.anko.doAsync
import java.util.concurrent.Phaser
import kotlin.reflect.full.primaryConstructor

class FragmentProfile : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val currentTerm = MutableLiveData<String?>()

	override fun getLayoutId() = R.layout.fragment_profile
	override fun getNavigationIcon() = R.drawable.ic_profile
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareTermsList()
		prepareGradesList()
		prepareMeetingsList()
		prepareShortcutsList()
		prepareClickListeners()
		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		prepareShortcutsList()

		root().horizontalProgressBar.isRefreshing = true
		prepareData {
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		root().buttonGrades.setOnClickListener { onChangeFragment?.invoke(FragmentGrades()) }
		root().buttonCourses.setOnClickListener { onChangeFragment?.invoke(FragmentCourses()) }
		root().buttonTests.setOnClickListener { onChangeFragment?.invoke(FragmentTests()) }
		root().buttonSurveys.setOnClickListener { onChangeFragment?.invoke(FragmentSurveys()) }
		root().buttonGuide.setOnClickListener { onChangeFragment?.invoke(FragmentGuide()) }
		root().buttonSettings.setOnClickListener { onChangeFragment?.invoke(FragmentSettings()) }
	}

	private fun prepareGradesList() {
		root().listRecentGrades.apply {
			setAdapter(AdapterRecentGrade().apply {
				onClickListener = {
					FragmentGrades.showGradeDialog(this@FragmentProfile, it, viewModelUser)
				}
			})
			setLayoutManager(
				LinearLayoutManager(
					requireContext(),
					RecyclerView.HORIZONTAL,
					false
				)
			)
		}
	}

	private fun prepareData(callback: (() -> Unit)? = null) {
		val loaded = Phaser()

		viewModelUser.getRecentGrades().observe(this) { (grades) ->
			root().listRecentGrades.setEmptyText(getString(R.string.no_recent_grades))
			(root().listRecentGrades.adapter as? AdapterRecentGrade)?.apply {
				update(grades.sortedBy { grade ->
					grade.dateModified
				})
			}

			grades.forEach {
				if (!it.examId.isNullOrEmpty() && it.examSessionNumber != 0) {
					loaded.register()
					viewModelUser.getGradeByExam(it.examId!!, it.examSessionNumber)
						.observe(this) { (grade) ->
							if (it.courseName?.isEmpty() != false) {
								it.courseName = grade?.courseName
								root().listRecentGrades.adapter?.notifyDataSetChanged()
							}
						}.onFinished { loaded.arriveAndDeregister() }
				}
			}
		}

		loaded.register()
		viewModelTimetable.getIncoming().observe(this) { (events) ->
			(root().listMeetings.adapter as? AdapterMeeting)?.update(events.filterNotNull())

			root().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
		}.onFinished { loaded.arriveAndDeregister() }

		loaded.register()
		viewModelUser.getAllGroups().observe(this) { (terms) ->
			postTerms(terms.map { group -> group.termId })
		}.onFinished { loaded.arriveAndDeregister() }

		loaded.register()
		prepareProfileData { loaded.arriveAndDeregister() }

		doAsync {
			loaded.arriveAndAwaitAdvance()
			callback?.invoke()
		}
	}

	private fun prepareMeetingsList() {
		root().listMeetings.apply {
			setAdapter(AdapterMeeting(true).apply {
				onAddToCalendarClickListener = {
					Utils.calendarIntent(requireContext(), it)
				}
				onRoomClickListener = {
					it.roomId?.apply { onChangeFragment?.invoke(FragmentRoom(this)) }
				}
			})
		}
	}

	private fun prepareTermsList() {
		root().listTerms.setAdapter(AdapterTerm())
	}

	private fun prepareShortcutsList() {
		for (i in (root().listShortcuts.size - 6) downTo 0) {
			root().listShortcuts.removeViewAt(i)
		}

		Preferences.get().getProfileShortcuts().also { list ->
			list.forEach { shortcut ->
				val className = shortcut[Preferences.KEY_PROFILE_SHORTCUTS_CLASS]
				val data = shortcut[Preferences.KEY_PROFILE_SHORTCUTS_DATA]
				if (className != null && data != null) {
					val inflater = LayoutInflater.from(requireContext())
					(Class.forName(className).kotlin.primaryConstructor
						?.call(data) as? FragmentPinnable)
						?.also { fragment ->
							val view = inflater.inflate(
								R.layout.list_item_shortcut,
								root().listShortcuts,
								false
							) as ViewGroup
							root().listShortcuts.addView(view, 0)
							fragment.getShortcut(this) { (icon, text) ->
								view.shortcutIcon.setImageResource(icon)
								view.shortcutText.text = text
							}
							view.setOnClickListener {
								onChangeFragment?.invoke(
									fragment as? FragmentBaseInterface ?: return@setOnClickListener
								)
							}
						}
				}
			}
		}
	}

	private fun postTerms(termIds: List<String>, callback: (() -> Unit)? = null) {
		viewModelUser.getTermsByIds(termIds).observe(this) { (terms) ->
			this.currentTerm.value = terms.minOrNull()?.id
			(root().listTerms.adapter as? AdapterTerm)?.update(terms)
		}.onFinished { callback?.invoke() }
	}

	private fun prepareProfileData(callback: (() -> Unit)? = null) {
		viewModelUser.getUserById().observe(this) { (user) ->
			root().userName.text = user?.name()
			root().userStudentNumber.text = user?.studentNumber
			user?.photoUrls?.values?.firstOrNull()?.also { image ->
				Picasso.get().load(image).into(root().userProfileImg)
			}
		}.onFinished { callback?.invoke() }
	}
}
