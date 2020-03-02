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
				callback(height - r.bottom)
			}
		}
	}

	open class SingletonHolder<T : Any, in A>(creator: (A?) -> T) {
		private var creator: ((A?) -> T)? = creator
		@Volatile
		protected var instance: T? = null

		fun init(arg: A? = null): T {
			if (instance != null) return instance!!

			return synchronized(this) {
				if (instance != null) instance!!
				else {
					val created = creator!!(arg)
					instance = created
					creator = null
					created
				}
			}
		}
	}
}