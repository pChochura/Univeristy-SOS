package com.pointlessapps.mobileusos.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Callback<T> {

	private var callback: ((T) -> Unit)? = null

	fun observe(callback: (T) -> Unit) {
		this.callback = callback
	}

	fun post(data: T) {
		GlobalScope.launch(Dispatchers.Main) {
			callback?.invoke(data ?: return@launch)
		}
	}
}