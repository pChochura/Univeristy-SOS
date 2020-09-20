package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isGone
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityMain
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS
import kotlinx.android.synthetic.main.fragment_browser.view.*

class FragmentBrowser(private val url: String) : FragmentBase() {

	override fun getLayoutId() = R.layout.fragment_browser

	override fun created() {
		prepareWebView()
		prepareClickListeners()
	}

	private fun prepareWebView() {
		root().webView.also {
			it.webViewClient = object : WebViewClient() {
				override fun shouldOverrideUrlLoading(
					view: WebView,
					request: WebResourceRequest
				): Boolean {
					root().progressBar.isGone = false

					if (request.url.scheme == HelperClientUSOS.CALLBACK_URL_HOST) {
						root().iconWelcome.isGone = false
						root().progressBar.isGone = true
						root().containerNotification.isGone = true
						HelperClientUSOS.handleLoginResult(requireActivity(), request.url) {
							startActivity(
								Intent(
									requireContext(),
									ActivityMain::class.java
								).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
							)
							requireActivity().finish()
						}

						return true
					}

					view.loadUrl(request.url.toString())
					return true
				}

				override fun onPageFinished(view: WebView, url: String) {
					root().progressBar.isGone = true
					super.onPageFinished(view, url)
				}
			}
			it.loadUrl(url)
		}
	}

	private fun prepareClickListeners() {
		root().buttonPrimary.setOnClickListener { root().containerNotification.isGone = true }
		root().buttonSecondary.setOnClickListener {
			HelperClientUSOS.handleLogin(
				requireActivity(),
				HelperClientUSOS.university ?: return@setOnClickListener
			)
		}
	}
}
