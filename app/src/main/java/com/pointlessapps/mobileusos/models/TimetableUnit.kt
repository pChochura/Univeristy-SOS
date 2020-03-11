package com.pointlessapps.mobileusos.models

import java.util.*

data class TimetableUnit(
	var userId: String?,
	var numberOfDays: Int,
	var startTime: Calendar
)