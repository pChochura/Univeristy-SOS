package com.pointlessapps.mobileusos.fragments

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.databinding.FragmentBrowserBinding
import com.pointlessapps.mobileusos.helpers.HelperClientUSOS

class FragmentBrowser(private val url: String) :
	FragmentCoreImpl<FragmentBrowserBinding>(FragmentBrowserBinding::class.java) {

	override fun created() {
		prepareWebView()
		prepareClickListeners()
	}

	@SuppressLint("SetJavaScriptEnabled")
	private fun prepareWebView() {
		binding().webView.also {
			it.webViewClient = object : WebViewClient() {
				override fun shouldOverrideUrlLoading(
					view: WebView,
					request: WebResourceRequest
				): Boolean {
					binding().progressBar.isGone = false

					if (request.url.scheme == HelperClientUSOS.CALLBACK_URL_HOST) {
						binding().iconWelcome.isVisible = true
						binding().progressBar.isGone = true
						binding().containerNotification.isGone = true
						HelperClientUSOS.handleLoginResult(requireActivity(), request.url)

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
			it.settings.javaScriptEnabled = true
			it.loadUrl(url)
		}
	}

	private fun prepareClickListeners() {
		binding().buttonPrimary.setOnClickListener { binding().containerNotification.isGone = true }
		binding().buttonSecondary.setOnClickListener {
			binding().webView.stopLoading()
			onForceGoBack?.invoke()
			HelperClientUSOS.handleLogin(
				requireActivity() as ComponentActivity,
				HelperClientUSOS.university ?: return@setOnClickListener
			)
		}
	}
}
