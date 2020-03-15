package com.pointlessapps.mobileusos.fragments

import android.os.Bundle
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pointlessapps.mobileusos.R

class FragmentSettings : PreferenceFragmentCompat(), FragmentBaseInterface {

	override fun getLayoutId() = R.layout.fragment_settings
	override fun getNavigationIcon() = R.drawable.ic_settings
	override fun getNavigationName() = R.string.settings
	override var bottomNavigationView: BottomNavigationView? = null
	override var onChangeFragmentListener: ((FragmentBaseInterface) -> Unit)? = null
	override var onLoadedFragmentListener: (() -> Unit)? = null

	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.fragment_settings, rootKey)
	}
}