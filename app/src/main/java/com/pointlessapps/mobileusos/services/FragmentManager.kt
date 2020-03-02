package com.pointlessapps.mobileusos.services

import android.view.Menu
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.exceptions.ExceptionFragmentContainerEmpty
import com.pointlessapps.mobileusos.fragments.FragmentBase
import org.jetbrains.anko.childrenRecursiveSequence
import org.jetbrains.anko.contentView

class FragmentManager private constructor(
	activity: FragmentActivity,
	private val fragmentManager: androidx.fragment.app.FragmentManager,
	private val fragments: Array<out FragmentBase>
) {
	private val history = mutableListOf<Int>()

	@IdRes
	private var containerId: Int? = null
	private var currentFragment: FragmentBase? = null
	private var bottomNavigation: BottomNavigationView? = null

	companion object {
		fun of(activity: FragmentActivity, fragment: FragmentBase, vararg fragments: FragmentBase) =
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
		fragments.forEach {
			it.onChangeFragmentListener = { fragment ->
				setFragment(fragment)
			}
			it.bottomNavigationView = bottomNavigation
		}
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
	}

	fun showIn(@IdRes containerId: Int) {
		this.containerId = containerId
		selectFirst()
	}

	fun selectFirst() = selectAt(0)
	fun selectMiddle() = selectAt(fragments.size / 2)
	fun selectLast() = selectAt(fragments.size - 1)
	fun selectAt(position: Int) = setFragment(fragments[position])

	private fun setFragment(fragment: FragmentBase) {
		if (containerId == null) {
			throw ExceptionFragmentContainerEmpty("Fragment container cannot be null.")
		}

		if (fragment == currentFragment) {
			return
		}

		fragmentManager.beginTransaction().apply {
			setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
			if (currentFragment != null) {
				replace(containerId!!, fragment)
			} else {
				add(containerId!!, fragment)
			}
			commit()
		}

		val fragmentIndex = fragments.indexOfFirst { it == fragment }
		if (fragmentIndex != -1) {
			currentFragment = fragment

			history.remove(fragmentIndex)
			history.add(fragmentIndex)

			bottomNavigation?.selectedItemId = fragmentIndex
		}
	}
}