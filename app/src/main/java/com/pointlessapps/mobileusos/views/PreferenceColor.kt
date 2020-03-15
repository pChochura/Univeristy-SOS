package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference

class PreferenceColor(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

	init {
//		Proponuję zrobić w taki sposób, że ikona to będzie plik ic_circle.xml, i po zmianie koloru wysatrczy zmienić tint dla ikony
//		setIcon(R.drawable.ic_circle)
//		icon.setTint(Color.parseColor("#123123"))
//		kolory zapisujesz w sharedPreferences
//		i będzie to coś w style persistInt(kolor) i getPersistedInt(domyslny_kolor)
	}

	override fun onClick() {
//		Tutaj po kliknięciu trzeba wyświetlić dialog z możliwością wyboru koloru
	}
}