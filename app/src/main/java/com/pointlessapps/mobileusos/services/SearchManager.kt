package com.pointlessapps.mobileusos.services

import com.google.android.material.textfield.TextInputLayout
import com.pointlessapps.mobileusos.utils.OnTextChanged

class SearchManager private constructor(textInputLayout: TextInputLayout) {

	private lateinit var onChangeTextListener: (String) -> Unit

	companion object {
		fun of(textInputLayout: TextInputLayout) = SearchManager(textInputLayout)
	}

	init {
		textInputLayout.editText?.addTextChangedListener(object : OnTextChanged {
			override fun textChanged(text: String) {
				onChangeTextListener.invoke(text)
			}
		})
	}

	fun setOnChangeTextListener(onChangeTextListener: (String) -> Unit) {
		this.onChangeTextListener = onChangeTextListener
	}
}