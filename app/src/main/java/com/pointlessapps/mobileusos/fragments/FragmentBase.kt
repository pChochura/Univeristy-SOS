package com.pointlessapps.mobileusos.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R

abstract class FragmentBase : Fragment() {

	private var rootView: ViewGroup? = null
	fun root() = rootView!!

	var bottomNavigationView: BottomNavigationView? = null
	var onChangeFragmentListener: ((FragmentBase) -> Unit)? = null
	var onLoadedFragmentListener: (() -> Unit)? = null
	var forceRefresh = false

	@LayoutRes
	abstract fun getLayoutId(): Int

	@DrawableRes
	open fun getNavigationIcon(): Int = 0

	@StringRes
	open fun getNavigationName(): Int = 0

	abstract fun created()
	open fun refreshed() = Unit

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null || forceRefresh) {
			forceRefresh = false
			rootView = inflater.inflate(getLayoutId(), container, false) as ViewGroup

			created()
		} else {
			refreshed()
		}
		return rootView
	}

	@SuppressLint("ResourceType")
	override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator? {
		return AnimatorInflater.loadAnimator(
			context!!,
			if (enter) {
				R.anim.fade_in
			} else {
				R.anim.fade_out
			}
		).apply {
			doOnEnd {
				onLoadedFragmentListener?.invoke()
			}
		}
	}

	override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
		var anim: Animation? = null
		try {
			anim = AnimationUtils.loadAnimation(context!!, transit).apply { setListener() }
		} catch (e: Exception) {
			try {
				anim = AnimationUtils.loadAnimation(context!!, nextAnim).apply { setListener() }
			} catch (e: Exception) {
			}
		}
		return anim
	}

	private fun Animation.setListener() {
		setAnimationListener(object : Animation.AnimationListener {
			override fun onAnimationRepeat(anim: Animation?) = Unit
			override fun onAnimationStart(anim: Animation?) = Unit
			override fun onAnimationEnd(anim: Animation?) {
				onLoadedFragmentListener?.invoke()
			}
		})
	}
}