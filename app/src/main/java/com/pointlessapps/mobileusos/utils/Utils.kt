package com.pointlessapps.mobileusos.utils

import android.graphics.Rect
import android.view.View
import android.view.Window
import org.jetbrains.anko.find

object Utils {
	fun getKeyboardHeight(window: Window, callback: (Int) -> Unit) {
		window.decorView.find<View>(android.R.id.content).apply {
			viewTreeObserver.addOnGlobalLayoutListener {
				val r = Rect()
				window.decorView.getWindowVisibleDisplayFrame(r)
				callback.invoke(height - r.bottom)
			}
		}
	}
}