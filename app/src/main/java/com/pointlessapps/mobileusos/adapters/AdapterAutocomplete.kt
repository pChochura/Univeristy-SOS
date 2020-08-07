package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Email
import org.jetbrains.anko.find

class AdapterAutocomplete(
	context: Context,
	val list: MutableList<Email.Recipient> = mutableListOf()
) :
	ArrayAdapter<Email.Recipient>(context, R.layout.list_item_autocomplete, list) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
		val viewGroup = convertView ?: LayoutInflater.from(context)
			.inflate(R.layout.list_item_autocomplete, null)

		viewGroup.find<AppCompatTextView>(R.id.text).text = getItem(position)?.name()

		return viewGroup!!
	}

	fun update(list: List<Email.Recipient>) {
		wholeList.apply {
			clear()
			addAll(list)
		}

		this.list.clear()
		this.list.addAll(wholeList)
		notifyDataSetChanged()
	}

	override fun getFilter(): Filter {
		return object : Filter() {
			override fun performFiltering(query: CharSequence?) = FilterResults().apply {
				wholeList.filter { it.name().contains(query ?: "") }.also {
					values = it
					count = it.count()
				}
			}

			override fun publishResults(sequence: CharSequence?, results: FilterResults?) {
				if (results?.count == 0) {
					notifyDataSetInvalidated()
				} else {
					clear()
					addAll(results?.values as? List<Email.Recipient> ?: wholeList)
					notifyDataSetChanged()
				}
			}

		}
	}
}
