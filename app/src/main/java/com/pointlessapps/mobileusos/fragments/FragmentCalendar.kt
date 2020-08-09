package com.pointlessapps.mobileusos.fragments

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEvent
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.models.User
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.jetbrains.anko.find
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.*

class FragmentCalendar : FragmentBase() {

	private val allEvents = mutableListOf<CalendarEvent>()
	private val viewModelCommon by viewModels<ViewModelCommon>()

	private var selectedDay = LocalDate.now()
	private var startDate = MutableLiveData<Date>(Calendar.getInstance().time)

	override fun getLayoutId() = R.layout.fragment_calendar
	override fun getNavigationIcon() = R.drawable.ic_calendar
	override fun getNavigationName() = R.string.calendar

	override fun created() {
		prepareCalendar()
		prepareEventsList()

		startDate.observe(this) {
			viewModelCommon.getCalendarByFaculties(listOf(User.Faculty.BASE_FACULTY_ID), it)
				.observe(this) { list ->
					root().loadingContainer.isVisible = false

					allEvents.addAll(list?.filter { event ->
						allEvents.find { it.id == event.id } == null
					} ?: return@observe)
					updateEventList()
					root().calendar.notifyCalendarChanged()
				}
		}
	}

	private fun prepareEventsList() {
		root().listEvents.apply {
			adapter = AdapterEvent()
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareCalendar() {
		root().calendar.dayBinder = object : DayBinder<DayViewContainer> {
			override fun create(view: View) = DayViewContainer(view)
			override fun bind(container: DayViewContainer, day: CalendarDay) {
				container.eventsContainer.apply {
					removeAllViews()
					getEventsFromDay(day.date).forEach {
						addView(View(requireContext()).apply {
							layoutParams = ViewGroup.LayoutParams(6.dp, 6.dp)
							setBackgroundResource(R.drawable.ic_circle)
							backgroundTintList = ColorStateList.valueOf(getColorForEvent(it))
						})
					}
				}

				container.dayNumber.apply {
					setTextColor(
						ContextCompat.getColor(
							requireContext(),
							if (day.date.dayOfWeek == DayOfWeek.SUNDAY) {
								R.color.colorTextRed
							} else {
								R.color.colorTextPrimary
							}
						)
					)
					alpha = if (day.owner == DayOwner.THIS_MONTH) {
						1f
					} else {
						0.3f
					}
					text = day.day.toString()

					setOnClickListener {
						selectedDay = day.date
						updateEventList()

						root().calendar.notifyCalendarChanged()
					}
				}

				container.border.isVisible =
					day.date.dayOfYear == selectedDay.dayOfYear && day.date.year == selectedDay.year
			}
		}

		root().calendar.monthScrollListener = { month ->
			startDate.value = Calendar.getInstance().apply {
				set(Calendar.YEAR, month.year)
				set(Calendar.MONTH, month.month)
				set(Calendar.DAY_OF_MONTH, 1)
			}.time
		}

		root().calendar.monthHeaderBinder = object : MonthHeaderFooterBinder<HeaderViewContainer> {
			override fun create(view: View) = HeaderViewContainer(view)
			override fun bind(container: HeaderViewContainer, month: CalendarMonth) {
				container.monthName.text =
					month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())

				container.buttonNext.setOnClickListener {
					root().calendar.scrollToMonth(month.yearMonth.plusMonths(1))
				}

				container.buttonPrevious.setOnClickListener {
					root().calendar.scrollToMonth(month.yearMonth.minusMonths(1))
				}
			}
		}

		val currentMonth = YearMonth.now()
		val firstMonth = currentMonth.minusMonths(10)
		val lastMonth = currentMonth.plusMonths(10)
		root().calendar.setup(firstMonth, lastMonth, DayOfWeek.MONDAY)
		root().calendar.scrollToMonth(YearMonth.now())
	}

	private fun getColorForEvent(event: CalendarEvent) = ContextCompat.getColor(
		requireContext(), when (event.type) {
			"break" -> R.color.color1
			"public_holidays" -> R.color.color2
			"exam_session" -> R.color.color3
			"academic_year" -> R.color.color4
			else -> R.color.colorAccent
		}
	)

	private fun updateEventList() {
		val events = getEventsFromDay(selectedDay)
		(root().listEvents.adapter as? AdapterEvent)?.update(events)
		root().labelEvents.isVisible = events.isNotEmpty()
	}

	private fun getEventsFromDay(day: LocalDate): List<CalendarEvent> {
		return allEvents.filter {
			day >= it.startDate?.toInstant()
				?.atZone(ZoneId.systemDefault())
				?.toLocalDate()
					&& day <= it.endDate?.toInstant()
				?.atZone(ZoneId.systemDefault())
				?.toLocalDate()
		}
	}

	class DayViewContainer(view: View) : ViewContainer(view) {
		val dayNumber = view.find<AppCompatTextView>(R.id.dayNumber)
		val border = view.find<View>(R.id.border)
		val eventsContainer = view.find<ViewGroup>(R.id.eventsContainer)
	}

	class HeaderViewContainer(view: View) : ViewContainer(view) {
		val monthName = view.find<AppCompatTextView>(R.id.monthName)
		val buttonNext = view.find<View>(R.id.buttonNext)
		val buttonPrevious = view.find<View>(R.id.buttonPrevious)
	}
}