package com.pointlessapps.mobileusos.models

import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

data class WidgetConfiguration(
	var visibleDays: Int = 3,
	var visibleHoursBefore: Int = 1,
	var visibleHoursAfter: Int = 4,
	var transparency: Int = 100
) {
	@ColorInt
	var futureBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var pastBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var weekendsBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var nowLineColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var todayHeaderTextColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var headerTextColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var headerBackgroundColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())

	@ColorInt
	var dividerLineColor: Int = 0
		get() = ColorUtils.setAlphaComponent(field, (transparency * 2.55f).toInt())
}