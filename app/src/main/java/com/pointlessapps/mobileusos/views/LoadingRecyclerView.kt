package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ViewLoadingRecyclerViewBinding
import com.pointlessapps.mobileusos.databinding.ViewLoadingRecyclerViewHorizontalBinding
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import org.jetbrains.anko.find
import org.jetbrains.anko.findOptional

class LoadingRecyclerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
	FrameLayout(context, attrs, defStyleAttr) {
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	private val binding: ViewBinding
	private var emptyText: String? = null

	@DrawableRes
	private var emptyIcon: Int? = null

	private var loaded = true

	val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
		get() = binding.root.find<RecyclerView>(R.id.recyclerView).adapter

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingRecyclerView, 0, 0)
		val orientation =
			a.getInt(R.styleable.LoadingRecyclerView_android_orientation, RecyclerView.VERTICAL)
		val padding =
			a.getDimension(R.styleable.LoadingRecyclerView_android_padding, 0f).toInt()
		a.recycle()

		binding = if (orientation == RecyclerView.VERTICAL) {
			ViewLoadingRecyclerViewBinding.inflate(
				LayoutInflater.from(context),
				this,
				true
			)
		} else {
			ViewLoadingRecyclerViewHorizontalBinding.inflate(
				LayoutInflater.from(context),
				this,
				true
			)
		}

		binding.root.find<RecyclerView>(R.id.recyclerView).layoutManager =
			UnscrollableLinearLayoutManager(context, orientation, false)
		binding.root.findOptional<View>(R.id.scrollView)?.also {
			setPadding(0)
			it.setPadding(padding)
		}

		ensureState()
	}

	fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
		binding.root.find<RecyclerView>(R.id.recyclerView).adapter = adapter
		adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() = ensureState()
		})

		ensureState()
	}

	private fun ensureState() {
		if (adapter?.itemCount == 0) {
			binding.root.find<RecyclerView>(R.id.recyclerView).isVisible = false
			emptyText.isNullOrEmpty().also {
				binding.root.find<ProgressBar>(R.id.progressBar).isVisible = it
				binding.root.find<RefreshProgressBar>(R.id.horizontalProgressBar).isRefreshing =
					!loaded
				binding.root.find<TextView>(R.id.textEmpty).isVisible = !it
				binding.root.find<ImageView>(R.id.iconEmpty).isVisible = emptyIcon !== null && !it
				binding.root.find<ViewGroup>(R.id.containerEmpty).isVisible = !it
			}
		} else {
			binding.root.find<RecyclerView>(R.id.recyclerView).isVisible = true
			binding.root.find<RefreshProgressBar>(R.id.horizontalProgressBar).isRefreshing = !loaded
			binding.root.find<TextView>(R.id.textEmpty).isGone = true
			binding.root.find<ImageView>(R.id.iconEmpty).isGone = true
			binding.root.find<ProgressBar>(R.id.progressBar).isGone = true
			binding.root.find<ViewGroup>(R.id.containerEmpty).isGone = true
		}
	}

	fun setCacheSize(size: Int) =
		binding.root.find<RecyclerView>(R.id.recyclerView).setItemViewCacheSize(size)

	fun setLayoutManager(layoutManager: LinearLayoutManager) {
		binding.root.find<RecyclerView>(R.id.recyclerView).layoutManager = layoutManager
	}

	fun setEmptyText(text: String) {
		binding.root.find<TextView>(R.id.textEmpty).text = text
		emptyText = text

		ensureState()
	}

	fun setEmptyIcon(@DrawableRes icon: Int, @ColorInt tint: Int? = null) {
		binding.root.find<ImageView>(R.id.iconEmpty).setImageResource(icon)
		tint?.also { binding.root.find<ImageView>(R.id.iconEmpty).setColorFilter(it) }
		emptyIcon = icon

		ensureState()
	}

	fun setLoaded(loaded: Boolean = true) {
		this.loaded = loaded
		ensureState()
	}
}
