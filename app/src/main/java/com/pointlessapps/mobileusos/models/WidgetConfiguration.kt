package com.pointlessapps.mobileusos.models

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

data class WidgetConfiguration(
	var visibleDays: Int = 3,
	var visibleHoursBefore: Int = 1,
	var visibleHoursAfter: Int = 4,
	var transparency: Int = 0,
	var eventTextSize: Int = 14
) {
	@ColorInt
	var futureBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var pastBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var weekendsBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var nowLineColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var todayHeaderTextColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var eventTextColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var headerTextColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var headerBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	@ColorInt
	var dividerLineColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, computeAndGetTransparency())

	private fun computeAndGetTransparency() = ((100 - transparency) * 2.55f).toInt()
}