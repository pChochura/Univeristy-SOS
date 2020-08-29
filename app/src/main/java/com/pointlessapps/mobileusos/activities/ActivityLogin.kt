package com.pointlessapps.mobileusos.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterUniversity
import com.pointlessapps.mobileusos.exceptions.ExceptionNullKeyOrSecret
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.helpers.getSystemDarkMode
import com.pointlessapps.mobileusos.services.SearchManager
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.dialog_pick_university.*

class ActivityLogin : FragmentActivity() {

	private val viewModelCommon by viewModels<ViewModelCommon>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		if (Preferences.get().getSystemDarkMode()) {
			setTheme(R.style.AppTheme_Dark)
		}

		setContentView(R.layout.activity_login)

		prepareClickListeners()
	}

	private fun prepareClickListeners() {
		buttonLogin.setOnClickListener {
			DialogUtil.create(this, R.layout.dialog_pick_university, { dialog ->
				if (applicationContext == null) {
					return@create
				}

				dialog.listUniversities.apply {
					setAdapter(AdapterUniversity().apply {
						onClickListener = { university ->
							if (university.consumerKey == null || university.consumerSecret == null) {
								throw ExceptionNullKeyOrSecret("Neither consumerKey nor consumerSecret can be null.")
							}

							HelperClientUSOS.handleLogin(this@ActivityLogin, university)

							dialog.dismiss()
						}
					})
				}

				viewModelCommon.getAllUniversities().observe(this) {
					(dialog.listUniversities.adapter as? AdapterUniversity)?.apply {
						update(it.first ?: listOf())
						showMatching(dialog.inputSearchUniversities.text.toString())
					}

					dialog.listUniversities.apply {
						setEmptyText(getString(R.string.no_universities))
						setEmptyIcon(R.drawable.ic_no_universities)

						setLoaded(it.second)
					}
				}

				SearchManager.of(dialog.inputSearchUniversities).setOnChangeTextListener {
					(dialog.listUniversities.adapter as? AdapterUniversity)?.showMatching(it)
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, 500.dp)
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
