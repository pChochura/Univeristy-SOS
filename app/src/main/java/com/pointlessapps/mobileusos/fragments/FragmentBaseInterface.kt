package com.pointlessapps.mobileusos.fragments

import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import com.google.android.material.bottomnavigation.BottomNavigationView

interface FragmentBaseInterface {
	@LayoutRes
	fun getLayoutId(): Int

	@DrawableRes
	fun getNavigationIcon(): Int = 0

	@StringRes
	fun getNavigationName(): Int = 0

	fun created() = Unit

	fun refreshed() = Unit

	fun root(): ViewGroup? = null

	var bottomNavigationView: BottomNavigationView?
	var onChangeFragmentListener: ((FragmentBaseInterface) -> Unit)?
	var onLoadedFragmentListener: (() -> Unit)?
}