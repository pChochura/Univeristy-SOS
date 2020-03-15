package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.preference.Preference
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.DialogUtil
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import org.jetbrains.anko.colorAttr

class PreferenceColor(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

	init {
//		Proponuję zrobić w taki sposób, że ikona to będzie plik ic_circle.xml, i po zmianie koloru wysatrczy zmienić tint dla ikony
		setIcon(R.drawable.ic_circle)
		val color = getPersistedInt(ContextCompat.getColor(context, R.color.color1))
		icon.setTint(color)
//		kolory zapisujesz w sharedPreferences
//		i będzie to coś w style persistInt(kolor) i getPersistedInt(domyslny_kolor)
	}

	override fun onClick() {
//		Tutaj po kliknięciu trzeba wyświetlić dialog z możliwością wyboru koloru
		DialogUtil.create(context, R.layout.dialog_color_picker, { dialog ->
//          Tutaj piszesz kod, który będzie zapisywał wybrany kolor itd
			for(i in 1..20) {
				val view = dialog.findViewById<AppCompatImageView>(context.resources.getIdentifier("imageColor$i", "id", context.packageName));
				view.setOnClickListener{
					val color = view.imageTintList?.defaultColor!!;
					persistInt(color);
					icon.setTint(color)
					dialog.hide()
				}
			}

		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}
}