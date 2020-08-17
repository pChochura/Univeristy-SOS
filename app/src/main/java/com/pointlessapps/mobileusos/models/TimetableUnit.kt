package com.pointlessapps.mobileusos.models

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

@Keep
data class TimetableUnit(
	@SerializedName("number_of_days")
	var numberOfDays: Int,
	@SerializedName("start_time")
	var startTime: Calendar
)
