package com.pointlessapps.mobileusos.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.models.University
import com.pointlessapps.mobileusos.services.SearchManager
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUniversity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class ActivityLogin : FragmentActivity() {

	private val viewModelUniversity by viewModels<ViewModelUniversity>()

	private val universities = mutableListOf<University>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		prepareListUniversity()
		prepareOnKeyboardPaddingChange()
		prepareSearch()

		viewModelUniversity.getAll().observe(this, Observer {
			if (it == null) {
				return@Observer
			}

			hideLoader()
			universities.clear()
			universities.addAll(it)
			listUniversities.adapter?.notifyDataSetChanged()
		})
	}

	private fun hideLoader() {
		progressLoading.visibility = View.GONE
	}

	private fun prepareSearch() {
		SearchManager.of(editSearch).setOnChangeTextListener {
			(listUniversities.adapter as AdapterUniversity).showMatching(it)
		}
	}

	private fun prepareOnKeyboardPaddingChange() {
		KeyboardVisibilityEvent.setEventListener(this, object : KeyboardVisibilityEventListener {
			override fun onVisibilityChanged(isOpen: Boolean) {
				val left = listUniversities.paddingLeft
				val right = listUniversities.paddingRight
				val top = listUniversities.paddingTop
				Utils.getKeyboardHeight(window) {
					listUniversities.setPadding(left, top, right, if (isOpen) it else 0)
				}
			}
		})
	}

	private fun prepareListUniversity() {
		listUniversities.apply {
			layoutManager = UnscrollableLinearLayoutManager(
				applicationContext,
				LinearLayoutManager.VERTICAL,
				false
			)
			adapter = AdapterUniversity(universities).apply {
				onClickListener = {

				}
			}
		}
	}
}