package com.pointlessapps.mobileusos.fragments

import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

interface FragmentCore<Binding : ViewBinding> {
	@DrawableRes
	fun getNavigationIcon(): Int = 0

	@StringRes
	fun getNavigationName(): Int = 0

	fun created() = Unit
	fun refreshed() = Unit

	fun binding(): Binding? = null

	var forceRefresh: Boolean

	var bottomNavigationView: BottomNavigationView?
	var onChangeFragment: ((FragmentCore<*>) -> Unit)?
	var onReplaceFragment: ((FragmentCore<*>) -> Unit)?
	var onForceRecreate: (() -> Unit)?
	var onForceRefreshAllFragments: (() -> Unit)?
	var onBackPressedListener: (() -> Boolean)?
	var onForceGoBack: (() -> Unit)?
	var onKeyboardStateChangedListener: ((Boolean) -> Unit)?
}
