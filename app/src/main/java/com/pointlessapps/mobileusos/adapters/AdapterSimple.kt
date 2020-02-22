package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class AdapterSimple<T>(protected val list: MutableList<T>) :
	RecyclerView.Adapter<DataObjectHolder>() {

	lateinit var onClickListener: (T) -> Unit

	abstract fun getLayoutId(): Int
	abstract fun onBind(root: View, position: Int)

	open fun onCreate(root: View, position: Int) = Unit

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {
		return DataObjectHolder(
			LayoutInflater.from(parent.context!!).inflate(
				getLayoutId(),
				parent,
				false
			),
			::onCreate
		)
	}

	override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
		onBind(holder.root, position)
	}

	override fun getItemCount() = list.size

	override fun getItemId(position: Int) =
		list[position].hashCode().toLong()

	open fun update(list: List<T>) {
		this.list.apply {
			clear()
			addAll(list)
		}
		notifyDataSetChanged()
	}
}

class DataObjectHolder(itemView: View, onCreateCallback: (View, Int) -> Unit) :
	RecyclerView.ViewHolder(itemView) {
	val root = itemView

	init {
		onCreateCallback.invoke(root, adapterPosition)
	}
}