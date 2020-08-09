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
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R

abstract class FragmentBase : Fragment(), FragmentBaseInterface {

	private var rootView: ViewGroup? = null
	override fun root() = rootView!!

	override var bottomNavigationView: BottomNavigationView? = null
	override var onChangeFragmentListener: ((FragmentBaseInterface) -> Unit)? = null
	override var onLoadedFragmentListener: (() -> Unit)? = null
	override var onBackPressedListener: (() -> Boolean)? = null
	override var onForceGoBackListener: (() -> Unit)? = null

	private var forceRefresh = false

	fun forceRefresh(force: Boolean = true) {
		forceRefresh = force
	}

	override fun forceGoBack(): Unit = onForceGoBackListener?.invoke() ?: Unit

	abstract override fun created()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (rootView == null || forceRefresh) {
			forceRefresh(false)
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
			requireContext(),
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
			anim = AnimationUtils.loadAnimation(requireContext(), transit).apply { setListener() }
		} catch (e: Exception) {
			try {
				anim =
					AnimationUtils.loadAnimation(requireContext(), nextAnim).apply { setListener() }
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
