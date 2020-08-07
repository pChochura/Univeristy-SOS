package com.pointlessapps.mobileusos.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import kotlinx.android.synthetic.main.view_loading_recycler_view.view.*

class LoadingRecyclerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
	FrameLayout(context, attrs, defStyleAttr) {
	constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

	private var emptyText: String? = null

	val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?
		get() = recyclerView.adapter

	init {
		val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingRecyclerView, 0, 0)
		val orientation =
			a.getInt(R.styleable.LoadingRecyclerView_android_orientation, RecyclerView.VERTICAL)
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
			if (emptyText.isNullOrEmpty()) {
				progressBar.visibility = View.VISIBLE
				textEmpty.visibility = View.INVISIBLE
			} else {
				progressBar.visibility = View.INVISIBLE
				textEmpty.visibility = View.VISIBLE
			}
		} else {
			recyclerView.visibility = View.VISIBLE
			textEmpty.visibility = View.GONE
			progressBar.visibility = View.GONE
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
}
