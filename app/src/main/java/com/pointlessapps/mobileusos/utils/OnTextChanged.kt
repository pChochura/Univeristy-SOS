package com.pointlessapps.mobileusos.utils

import android.text.Editable
import android.text.TextWatcher

interface OnTextChanged : TextWatcher {

	override fun afterTextChanged(p0: Editable) = textChanged(p0.toString())
	override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
	override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

	fun textChanged(text: String)
}