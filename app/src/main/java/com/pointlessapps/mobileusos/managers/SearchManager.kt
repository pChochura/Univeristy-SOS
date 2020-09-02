package com.pointlessapps.mobileusos.managers

import android.widget.TextView
import com.pointlessapps.mobileusos.utils.OnTextChanged

class SearchManager private constructor(textView: TextView) {

	private lateinit var onChangeTextListener: (String) -> Unit

	companion object {
		fun of(textView: TextView) = SearchManager(textView)
	}

	init {
		textView.addTextChangedListener(object : OnTextChanged {
			override fun textChanged(text: String) {
				onChangeTextListener.invoke(text)
			}
		})
	}

	fun setOnChangeTextListener(onChangeTextListener: (String) -> Unit) {
		this.onChangeTextListener = onChangeTextListener
	}
}
