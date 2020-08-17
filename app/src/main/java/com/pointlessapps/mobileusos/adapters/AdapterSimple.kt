package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import org.jetbrains.anko.find

abstract class AdapterSimple<T>(protected open val list: MutableList<T>) :
	RecyclerView.Adapter<DataObjectHolder>() {

	var onClickListener: ((T) -> Unit)? = null
	private var collapsed = true

	abstract fun getLayoutId(viewType: Int): Int
	abstract fun onBind(root: View, position: Int)

	open fun onCollapseMaxItemCount() = 3
	open fun isCollapsible() = false
	open fun onCreate(root: View) = Unit

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataObjectHolder {
		return DataObjectHolder(
			LayoutInflater.from(parent.context!!).inflate(
				if (isCollapsible()) {
					getInternalLayoutId(viewType)
				} else {
					getLayoutId(viewType)
				},
				parent,
				false
			)
		) { view, _ ->
			if (!isCollapsible() || viewType == +ViewType.SIMPLE) {
				onCreate(view)
			} else {
				view.find<View>(R.id.buttonCollapse).setOnClickListener {
					collapsed = !collapsed
					notifyDataSetChanged()
				}
			}
		}
	}

	private fun getInternalLayoutId(viewType: Int) = when (viewType) {
		+ViewType.SIMPLE -> getLayoutId(viewType)
		+ViewType.SHOW_MORE -> R.layout.list_item_show_more
		else -> R.layout.list_item_show_less
	}

	override fun onBindViewHolder(holder: DataObjectHolder, position: Int) {
		if (getItemViewType(position) == +ViewType.SIMPLE) {
			onBind(holder.root, position)
		}
	}

	override fun getItemCount() = when {
		isCollapsed() -> onCollapseMaxItemCount() + 1
		isCollapsibleInternal() && list.size > 0 -> list.size + 1
		else -> list.size
	}

	override fun getItemId(position: Int) = when {
		!hasStableIds() -> RecyclerView.NO_ID
		position in 0 until list.size -> list[position].hashCode().toLong()
		else -> position.toLong()
	}

	override fun getItemViewType(position: Int) =
		if (!hasStableIds()) {
			+ViewType.SIMPLE
		} else if (isCollapsed() && position == itemCount - 1) {
			+ViewType.SHOW_MORE
		} else if (isCollapsibleInternal() && !isCollapsed() && position == itemCount - 1) {
			+ViewType.SHOW_LESS
		} else {
			+ViewType.SIMPLE
		}

	private fun isCollapsed() = collapsed && isCollapsibleInternal()
	private fun isCollapsibleInternal() = isCollapsible() && (list.size > onCollapseMaxItemCount())

	open fun update(list: List<T>) {
		this.list.apply {
			clear()
			addAll(list)
		}
		notifyDataSetChanged()
	}

	enum class ViewType {
		SIMPLE, SHOW_MORE, SHOW_LESS, ADD;

		operator fun unaryPlus() = ordinal
	}
}

class DataObjectHolder(itemView: View, onCreateCallback: (View, Int) -> Unit) :
	RecyclerView.ViewHolder(itemView) {
	val root = itemView

	init {
		onCreateCallback.invoke(root, bindingAdapterPosition)
	}
}
