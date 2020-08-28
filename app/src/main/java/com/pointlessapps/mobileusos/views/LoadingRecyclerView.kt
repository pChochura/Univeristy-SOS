package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import kotlinx.android.synthetic.main.view_loading_recycler_view.view.*
import org.jetbrains.anko.findOptional

class LoadingRecyclerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
	FrameLayout(context, attrs, defStyleAttr) {
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	private var emptyText: String? = null

	@DrawableRes
	private var emptyIcon: Int? = null

	private var loaded = true

	val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
		get() = recyclerView.adapter

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingRecyclerView, 0, 0)
		val orientation =
			a.getInt(R.styleable.LoadingRecyclerView_android_orientation, RecyclerView.VERTICAL)
		val padding =
			a.getDimension(R.styleable.LoadingRecyclerView_android_padding, 0f).toInt()
		a.recycle()

		View.inflate(
			context,
			if (orientation == RecyclerView.VERTICAL) {
				R.layout.view_loading_recycler_view
			} else {
				R.layout.view_loading_recycler_view_horizontal
			},
			this
		)

		recyclerView.layoutManager = UnscrollableLinearLayoutManager(context, orientation, false)
		findOptional<View>(R.id.scrollView)?.also {
			setPadding(0)
			it.setPadding(padding)
		}

		ensureState()
	}

	fun setAdapter(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>) {
		recyclerView.adapter = adapter
		adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
			override fun onChanged() = ensureState()
		})

		ensureState()
	}

	private fun ensureState() {
		if (adapter?.itemCount == 0) {
			recyclerView.visibility = View.INVISIBLE
			emptyText.isNullOrEmpty().also {
				progressBar.isVisible = it
				horizontalProgressBar.isInvisible = loaded
				textEmpty.isVisible = !it
				iconEmpty.isVisible = emptyIcon !== null && !it
			}
		} else {
			recyclerView.isVisible = true
			horizontalProgressBar.isGone = loaded
			textEmpty.isGone = true
			iconEmpty.isGone = true
			progressBar.isGone = true
		}
	}

	fun setLayoutManager(layoutManager: LinearLayoutManager) {
		recyclerView.layoutManager = layoutManager
	}

	fun setEmptyText(text: String) {
		textEmpty.text = text
		emptyText = text

		ensureState()
	}

	fun setEmptyIcon(@DrawableRes icon: Int) {
		iconEmpty.setImageResource(icon)
		emptyIcon = icon

		ensureState()
	}

	fun setLoaded(loaded: Boolean = true) {
		this.loaded = loaded
		ensureState()
	}
}
