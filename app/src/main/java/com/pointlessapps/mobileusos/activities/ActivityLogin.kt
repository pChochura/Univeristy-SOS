package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.services.SearchManager
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelLogin
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class ActivityLogin : FragmentActivity() {

	private val viewModelLogin by viewModels<ViewModelLogin>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_login)

		prepareListUniversity()
		prepareOnKeyboardPaddingChange()
		prepareSearch()

		viewModelLogin.getAll().observe(this) {
			if (it == null) {
				return@observe
			}

			hideLoader()
			(listUniversities.adapter as? AdapterUniversity)?.update(it)
		}
	}

	private fun hideLoader() {
		progressLoading.visibility = View.GONE
	}

	private fun prepareSearch() {
		SearchManager.of(editSearch).setOnChangeTextListener {
			(listUniversities.adapter as? AdapterUniversity)?.showMatching(it)
		}
	}

	private fun prepareOnKeyboardPaddingChange() {
		KeyboardVisibilityEvent.setEventListener(
			this,
			this,
			object : KeyboardVisibilityEventListener {
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
			adapter = AdapterUniversity().apply {
				onClickListener = {
					if (it.consumerKey == null || it.consumerSecret == null) {
						throw ExceptionNullKeyOrSecret("Neither consumerKey nor consumerSecret can be null.")
					}

					HelperClientUSOS.handleLogin(this@ActivityLogin, it)
				}
			}
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		HelperClientUSOS.handleLoginResult(this, intent) {
			startActivity(Intent(this, ActivityMain::class.java))
			finish()
		}
	}
}