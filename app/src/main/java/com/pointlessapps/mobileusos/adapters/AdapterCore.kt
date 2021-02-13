package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemShowLessBinding
import com.pointlessapps.mobileusos.databinding.ListItemShowMoreBinding
import org.jetbrains.anko.find

abstract class AdapterCore<T, Binding : ViewBinding>(
	protected open val list: MutableList<T>,
	private val bindingClass: Class<Binding>
) : RecyclerView.Adapter<AdapterCore.ViewHolder<Binding>>() {

	var onClickListener: ((T) -> Unit)? = null
	private var collapsed = true

	abstract fun onBind(binding: Binding, position: Int)

	open fun onCollapseMaxItemCount() = 3
	open fun isCollapsible() = false
	open fun onCreate(binding: Binding) = Unit
	open fun getBindingClass(viewType: Int): Class<out ViewBinding> = bindingClass

	@Suppress("UNCHECKED_CAST")
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Binding> {
		val internalBindingClass = when {
			isCollapsible() -> getInternalBindingClass(viewType)
			else -> getBindingClass(viewType)
		}
		val binding = internalBindingClass.getMethod(
			"inflate",
			LayoutInflater::class.java,
			ViewGroup::class.java,
			Boolean::class.java
		).invoke(null, LayoutInflater.from(parent.context), parent, false) as Binding

		return ViewHolder(binding) {
			binding.root.setOnClickListener {
				onClickListener?.invoke(list[bindingAdapterPosition])
			}

			if (!isCollapsible() || viewType == +ViewType.SIMPLE) {
				onCreate(binding)

				return@ViewHolder
			}

			binding.root.find<View>(R.id.buttonCollapse).setOnClickListener {
				collapsed = !collapsed
				notifyDataSetChanged()
			}
		}
	}

	private fun getInternalBindingClass(viewType: Int) = when (viewType) {
		+ViewType.SIMPLE -> getBindingClass(viewType)
		+ViewType.SHOW_MORE -> ListItemShowMoreBinding::class.java
		else -> ListItemShowLessBinding::class.java
	}

	override fun onBindViewHolder(holder: ViewHolder<Binding>, position: Int) {
		if (getItemViewType(position) == +ViewType.SIMPLE) {
			onBind(holder.binding, position)
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

	internal enum class ViewType {
		SIMPLE, SHOW_MORE, SHOW_LESS, ADD;

		operator fun unaryPlus() = ordinal
	}

	class ViewHolder<Binding : ViewBinding>(
		val binding: Binding,
		onCreatedListener: (ViewHolder<Binding>.() -> Unit)? = null
	) : RecyclerView.ViewHolder(binding.root) {
		init {
			onCreatedListener?.invoke(this)
		}
	}
}
