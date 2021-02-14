package com.pointlessapps.mobileusos.fragments

import android.view.LayoutInflater
import androidx.core.view.size
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterMeeting
import com.pointlessapps.mobileusos.adapters.AdapterRecentGrade
import com.pointlessapps.mobileusos.adapters.AdapterTerm
import com.pointlessapps.mobileusos.databinding.FragmentProfileBinding
import com.pointlessapps.mobileusos.databinding.ListItemShortcutBinding
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getProfileShortcuts
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import java.util.*
import java.util.concurrent.Phaser
import kotlin.reflect.full.primaryConstructor

class FragmentProfile :
	FragmentCoreImpl<FragmentProfileBinding>(FragmentProfileBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val viewModelTimetable by viewModels<ViewModelTimetable>()
	private val currentTerm = MutableLiveData<String?>()

	override fun getNavigationIcon() = R.drawable.ic_profile
	override fun getNavigationName() = R.string.profile

	override fun created() {
		prepareTermsList()
		prepareGradesList()
		prepareMeetingsList()
		prepareShortcutsList()
		prepareClickListeners()
		refreshed()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		prepareShortcutsList()

		binding().horizontalProgressBar.isRefreshing = true
		prepareData {
			binding().pullRefresh.isRefreshing = false
			binding().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareClickListeners() {
		binding().containerShortcuts.buttonGrades.setOnClickListener {
			onChangeFragment?.invoke(FragmentGrades())
		}
		binding().containerShortcuts.buttonCourses.setOnClickListener {
			onChangeFragment?.invoke(FragmentCourses())
		}
		binding().containerShortcuts.buttonTests.setOnClickListener {
			onChangeFragment?.invoke(FragmentTests())
		}
		binding().containerShortcuts.buttonSurveys.setOnClickListener {
			onChangeFragment?.invoke(FragmentSurveys())
		}
		binding().containerShortcuts.buttonGuide.setOnClickListener {
			onChangeFragment?.invoke(FragmentGuide())
		}
		binding().buttonSettings.setOnClickListener {
			onChangeFragment?.invoke(FragmentSettings())
		}
	}

	private fun prepareGradesList() {
		binding().listRecentGrades.apply {
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
			binding().listRecentGrades.setEmptyText(getString(R.string.no_recent_grades))
			(binding().listRecentGrades.adapter as? AdapterRecentGrade)?.apply {
				binding().listRecentGrades.setCacheSize(grades.size)
				update(grades.sortedBy(Grade::dateModified))
			}

			grades.forEach {
				if (!it.examId.isNullOrEmpty() && it.examSessionNumber != 0) {
					loaded.register()
					viewModelUser.getGradeByExam(it.examId!!, it.examSessionNumber)
						.observe(this) { (grade) ->
							if (it.courseName?.isEmpty() != false) {
								it.courseName = grade?.courseName
								binding().listRecentGrades.adapter?.notifyDataSetChanged()
							}
						}.onFinished { loaded.arriveAndDeregister() }
				}
			}
		}

		loaded.register()
		viewModelTimetable.getIncoming().observe(this) { (events) ->
			val calendarTime = Calendar.getInstance().timeInMillis
			(binding().listMeetings.adapter as? AdapterMeeting)?.update(events.filterNotNull().filter { it.startTime.time >= calendarTime })

			binding().listMeetings.setEmptyText(getString(R.string.no_incoming_meetings))
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
		binding().listMeetings.apply {
			setAdapter(AdapterMeeting(true).apply {
				onClickListener =
					{ Utils.showCourseInfo(requireContext(), it, viewModelUser, onChangeFragment) }
			})
		}
	}

	private fun prepareTermsList() {
		binding().listTerms.setAdapter(AdapterTerm())
	}

	private fun prepareShortcutsList() {
		for (i in (binding().containerShortcuts.listShortcuts.size - 6) downTo 0) {
			binding().containerShortcuts.listShortcuts.removeViewAt(i)
		}

		Preferences.get().getProfileShortcuts().onEach { shortcut ->
			val className = shortcut[Preferences.KEY_PROFILE_SHORTCUTS_CLASS]
			val data = shortcut[Preferences.KEY_PROFILE_SHORTCUTS_DATA]
			if (className != null && data != null) {
				val inflater = LayoutInflater.from(requireContext())
				(Class.forName(className).kotlin.primaryConstructor
					?.call(data) as? FragmentPinnable)
					?.also { fragment ->
						val view = ListItemShortcutBinding.inflate(
							inflater,
							binding().containerShortcuts.listShortcuts,
							false
						)
						binding().containerShortcuts.listShortcuts.addView(view.root, 0)
						fragment.getShortcut(this) { (icon, text) ->
							view.shortcutIcon.setImageResource(icon)
							view.shortcutText.text = text
						}
						view.root.setOnClickListener {
							onChangeFragment?.invoke(
								fragment as? FragmentCore<*> ?: return@setOnClickListener
							)
						}
					}
			}
		}
	}

	private fun postTerms(termIds: List<String>, callback: (() -> Unit)? = null) {
		viewModelUser.getTermsByIds(termIds).observe(this) { (terms) ->
			this.currentTerm.value = terms.minOrNull()?.id
			(binding().listTerms.adapter as? AdapterTerm)?.update(terms)
		}.onFinished { callback?.invoke() }
	}

	private fun prepareProfileData(callback: (() -> Unit)? = null) {
		viewModelUser.getUserById().observe(this) { (user) ->
			binding().userName.text = user?.name()
			binding().userStudentNumber.text = user?.studentNumber
			user?.photoUrls?.values?.firstOrNull()?.also { image ->
				Picasso.get().load(image).into(binding().userProfileImg)
			}
		}.onFinished { callback?.invoke() }
	}
}
