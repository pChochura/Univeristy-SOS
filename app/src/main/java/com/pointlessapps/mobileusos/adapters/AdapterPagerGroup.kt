package com.pointlessapps.mobileusos.adapters

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.pointlessapps.mobileusos.fragments.FragmentPageGroup
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.models.Group
import com.pointlessapps.mobileusos.models.Term

class AdapterPagerGroup(fragmentManager: FragmentManager) :
	FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val list = mutableListOf<Pair<Term, List<Group>>>()

	fun update(list: List<Group>) {
		this.list.apply {
			clear()
			addAll(list.groupBy { Term(id = it.termId) }.toList())
			sortBy { it.first }
		}
		notifyDataSetChanged()
	}

	fun updateTerms(terms: List<Term>) {
		terms.associateBy { it.id }.also {
			list.forEach { entry ->
				entry.first.set(it[entry.first.id])
			}
		}
		list.sortBy { it.first }
		notifyDataSetChanged()
	}

	fun updateGrades(grades: List<Grade>) {
		grades.associateBy { it.courseId to it.termId }.also { gradesMap ->
			list.forEach {
				it.second.forEach { group ->
					group.grade = gradesMap[group.courseId to group.termId]
				}
			}
		}
		notifyDataSetChanged()
	}

	override fun getItem(position: Int) =
		FragmentPageGroup(list[position].second)

	override fun getCount() = list.size

	override fun getPageTitle(position: Int) = list[position].first.id

	override fun saveState(): Parcelable? = null

	override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
		try {
			super.restoreState(state, loader)
		} catch (e: Exception) {
			Log.e("AdapterPagerGroup", e.message ?: "RestoreState failed", e)
		}
	}
}