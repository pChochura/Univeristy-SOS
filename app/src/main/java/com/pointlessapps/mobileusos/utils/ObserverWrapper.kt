package com.pointlessapps.mobileusos.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

class ObserverWrapper<T>(
	private val liveData: MutableLiveData<Pair<T, SourceType>> = MutableLiveData(),
	block: (ObserverWrapper<T>.() -> Unit)? = null
) {

	private var onOnceCallback: ((Pair<T, SourceType>) -> Unit)? = null
	private var onFinishedCallback: ((Throwable?) -> Unit)? = null
	private var finished = false
	private var observerSet = false

	init {
		block?.invoke(this)
	}

	fun onFinished(callback: (Throwable?) -> Unit) {
		onFinishedCallback = callback
	}

	fun observe(
		owner: LifecycleOwner,
		observer: (Pair<T, SourceType>) -> Unit
	): ObserverWrapper<T> {
		observerSet = true
		liveData.observe(owner) {
			observer(it)
			if (it.second === SourceType.ONLINE && !finished) {
				finished()
			}
		}
		return this@ObserverWrapper
	}

	fun onOnceCallback(callback: (Pair<T, SourceType>) -> Unit): ObserverWrapper<T> {
		this.onOnceCallback = {
			callback(it)
			if (it.second === SourceType.ONLINE && !finished) {
				finished()
			}
		}
		return this
	}

	fun postValue(sourceType: SourceType = SourceType.OFFLINE, valueCallback: suspend () -> T) {
		GlobalScope.launch {
			(runCatching {
				withTimeout(TimeUnit.SECONDS.toMillis(10)) { valueCallback() }
			}.getOrElse {
				finished(it)
				return@launch
			} to sourceType).also {
				when {
					onOnceCallback !== null -> onOnceCallback?.invoke(it)
					observerSet -> liveData.postValue(it)
					!finished -> finished(null)
				}
			}
		}
	}

	private fun finished(throwable: Throwable? = null) {
		GlobalScope.launch(Dispatchers.Main) {
			onFinishedCallback?.invoke(throwable)
			finished = true
		}
	}
}

enum class SourceType {
	ONLINE, OFFLINE
}
