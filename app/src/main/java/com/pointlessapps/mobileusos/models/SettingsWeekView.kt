package com.pointlessapps.mobileusos.models

import androidx.annotation.IntRange

data class SettingsWeekView(
	@IntRange(from = 0, to = 23)
	var startHour: Int = DEFAULT_START_HOUR,
	@IntRange(from = 1, to = 24)
	var endHour: Int = DEFAULT_END_HOUR,
	@IntRange(from = 3, to = 7)
	var numberOfVisibleDays: Int = DEFAULT_NUMBER_OF_VISIBLE_DAYS,
	@IntRange(from = 12, to = 20)
	var eventTextSize: Int = DEFAULT_EVENT_TEXT_SIZE
) {

	companion object {
		const val DEFAULT_START_HOUR = 6
		const val DEFAULT_END_HOUR = 20
		const val DEFAULT_NUMBER_OF_VISIBLE_DAYS = 5
		const val DEFAULT_EVENT_TEXT_SIZE = 12
	}
}