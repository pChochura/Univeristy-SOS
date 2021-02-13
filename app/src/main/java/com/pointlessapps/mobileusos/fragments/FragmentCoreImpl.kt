package com.pointlessapps.mobileusos.fragments

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R

abstract class FragmentCoreImpl<Binding : ViewBinding>(private val bindingClass: Class<Binding>) :
	Fragment(), FragmentCore<Binding> {

	private var binding: Binding? = null
	override fun binding() = binding!!

	override var bottomNavigationView: BottomNavigationView? = null
	override var onForceGoBack: (() -> Unit)? = null
	override var onForceRecreate: (() -> Unit)? = null
	override var onChangeFragment: ((FragmentCore<*>) -> Unit)? = null
	override var onReplaceFragment: ((FragmentCore<*>) -> Unit)? = null
	override var onForceRefreshAllFragments: (() -> Unit)? = null
	override var onBackPressedListener: (() -> Boolean)? = null
	override var onKeyboardStateChangedListener: ((Boolean) -> Unit)? = null

	override var forceRefresh = false

	fun forceRefresh(force: Boolean = true) {
		forceRefresh = force
	}

	@Suppress("UNCHECKED_CAST")
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		if (binding == null || forceRefresh) {
			Log.d("LOG!", "class: $bindingClass")
			binding = bindingClass.getMethod(
				"inflate",
				LayoutInflater::class.java,
				ViewGroup::class.java,
				Boolean::class.java
			).invoke(null, inflater, container, false) as Binding

			created()
		} else {
			refreshed()
		}

		return binding().root
	}

	override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator =
		AnimatorInflater.loadAnimator(
			requireContext(),
			if (enter) {
				R.anim.fade_in
			} else {
				R.anim.fade_out
			}
		)

	override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int) =
		runCatching { AnimationUtils.loadAnimation(requireContext(), transit) }.getOrElse {
			runCatching { AnimationUtils.loadAnimation(requireContext(), nextAnim) }.getOrNull()
		}
}
