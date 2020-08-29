package com.pointlessapps.mobileusos.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class UnscrollableLinearLayoutManager(context: Context, orientation: Int, reverse: Boolean) :
	LinearLayoutManager(context, orientation, reverse) {
	private var canScroll = false

	override fun canScrollVertically() = canScroll
	override fun canScrollHorizontally() = canScroll
}
