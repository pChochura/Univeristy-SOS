package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.services.SearchManager
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUniversity
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import org.jetbrains.anko.find

class ActivityLogin : FragmentActivity() {

	private val viewModelUniversity by viewModels<ViewModelUniversity>()

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
			(listUniversities.adapter as? AdapterUniversity)?.update(it)
		})
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
		KeyboardVisibilityEvent.setEventListener(this, this, object : KeyboardVisibilityEventListener {
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

					HelperClientUSOS.handleLogin(
						this@ActivityLogin,
						it.url,
						it.consumerKey!!,
						it.consumerSecret!!
					)
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