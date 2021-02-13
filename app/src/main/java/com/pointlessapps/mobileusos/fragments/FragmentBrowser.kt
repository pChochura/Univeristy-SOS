package com.pointlessapps.mobileusos.fragments

import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isGone
import com.pointlessapps.mobileusos.activities.ActivityMain
import com.pointlessapps.mobileusos.databinding.FragmentBrowserBinding
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS

class FragmentBrowser(private val url: String) :
	FragmentCoreImpl<FragmentBrowserBinding>(FragmentBrowserBinding::class.java) {

	override fun created() {
		prepareWebView()
		prepareClickListeners()
	}

	private fun prepareWebView() {
		binding().webView.also {
			it.webViewClient = object : WebViewClient() {
				override fun shouldOverrideUrlLoading(
					view: WebView,
					request: WebResourceRequest
				): Boolean {
					binding().progressBar.isGone = false

					if (request.url.scheme == HelperClientUSOS.CALLBACK_URL_HOST) {
						binding().iconWelcome.isGone = false
						binding().progressBar.isGone = true
						binding().containerNotification.isGone = true
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
					binding().progressBar.isGone = true
					super.onPageFinished(view, url)
				}
			}
			it.loadUrl(url)
		}
	}

	private fun prepareClickListeners() {
		binding().buttonPrimary.setOnClickListener { binding().containerNotification.isGone = true }
		binding().buttonSecondary.setOnClickListener {
			HelperClientUSOS.handleLogin(
				requireActivity(),
				HelperClientUSOS.university ?: return@setOnClickListener
			)
		}
	}
}
