package com.pointlessapps.mobileusos.managers

import android.content.Intent
import android.view.Menu
import androidx.annotation.IdRes
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.exceptions.ExceptionFragmentContainerEmpty
import com.pointlessapps.mobileusos.fragments.FragmentCore
import com.pointlessapps.mobileusos.fragments.FragmentCoreImpl
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.contentView

class FragmentManager private constructor(
	private val activity: FragmentActivity,
	private val fragmentManager: androidx.fragment.app.FragmentManager,
	private val fragments: Array<out FragmentCore<*>>
) {
	private val history = mutableListOf<FragmentCore<*>>()

	private var startingPosition = -1

	@IdRes
	private var containerId: Int? = null
	private var bottomNavigation: BottomNavigationView? = null
	var currentFragment: FragmentCore<*>? = null
		private set

	companion object {
		fun of(
			activity: FragmentActivity,
			fragment: FragmentCore<*>,
			vararg fragments: FragmentCore<*>
		) =
			FragmentManager(
				activity,
				activity.supportFragmentManager,
				arrayOf(fragment, *fragments)
			)
	}

	init {
		activity.contentView?.rootView?.childrenRecursiveSequence()?.forEach {
			if (it is BottomNavigationView) {
				bottomNavigation = it
				return@forEach
			}
		}

		fragments.forEach(::prepareFragment)

		bottomNavigation?.apply {
			fragments.forEachIndexed { index, fragment ->
				menu.add(Menu.NONE, index, Menu.NONE, fragment.getNavigationName())
					.setIcon(fragment.getNavigationIcon())
			}
			setOnNavigationItemSelectedListener {
				selectAt(it.itemId)
				true
			}
		}

		TedKeyboardObserver(activity)
			.listen { isShow ->
				fragments.forEach { it.onKeyboardStateChangedListener?.invoke(isShow) }
				bottomNavigation?.isGone = isShow
			}
	}

	private fun prepareFragment(fragment: FragmentCore<*>) {
		fragment.onChangeFragment = { setFragment(it.apply { prepareFragment(this) }) }
		fragment.onReplaceFragment = { setFragment(it.apply { prepareFragment(this) }, false) }
		fragment.onForceRecreate = {
			activity.startActivity(
				Intent(
					activity,
					activity::class.java
				)
			)
			activity.overridePendingTransition(0, 0)
			activity.finish()
		}
		fragment.onForceRefreshAllFragments = {
			(fragment as FragmentCoreImpl<*>).forceRefresh()
			history.forEach { (it as FragmentCoreImpl<*>).forceRefresh() }
		}
		fragment.onForceGoBack = { popHistory(true) }
		fragment.bottomNavigationView = bottomNavigation
	}

	fun showIn(@IdRes containerId: Int) {
		this.containerId = containerId
		selectFirst()
	}

	fun selectFirst() = selectAt(0)
	fun selectMiddle() = selectAt(fragments.size / 2)
	fun selectLast() = selectAt(fragments.size - 1)
	fun selectAt(position: Int) {
		setFragment(fragments[position])
		startingPosition = if (startingPosition == -1) position else startingPosition
	}

	fun changeFragment(fragment: FragmentCore<*>) =
		setFragment(fragment.apply { prepareFragment(this) })

	private fun setFragment(fragment: FragmentCore<*>, addToHistory: Boolean = true) {
		if (containerId === null) {
			throw ExceptionFragmentContainerEmpty("Fragment container cannot be null.")
		}

		if (fragment === currentFragment) {
			return
		}

		currentFragment = fragment

		fragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
			if (currentFragment !== null) {
				replace(containerId!!, fragment as Fragment)
			} else {
				add(containerId!!, fragment as Fragment)
			}
			commit()
		}

		val fragmentIndex = fragments.indexOfFirst { it === fragment }
		if (fragmentIndex != -1) {
			if (addToHistory) {
				history.remove(fragment)
			}

			bottomNavigation?.menu?.setGroupCheckable(0, true, true)
			bottomNavigation?.selectedItemId = fragmentIndex
		} else {
			bottomNavigation?.menu?.setGroupCheckable(0, false, true)
		}

		if (addToHistory) {
			history.add(fragment)
		}
	}

	fun popHistory(force: Boolean = false): Boolean {
		if (!force && currentFragment?.onBackPressedListener?.invoke() == true) {
			return true
		}

		if (startingPosition == -1 ||
			(history.size <= 1 && currentFragment === fragments[startingPosition])
		) {
			return false
		}

		if (history.size <= 1) {
			setFragment(fragments[startingPosition], false)
			history.removeAt(history.lastIndex)

			return true
		}

		history.removeAt(history.lastIndex)
		setFragment(history.last(), false)

		return true
	}
}
