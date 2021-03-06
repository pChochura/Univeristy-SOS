package com.pointlessapps.mobileusos.fragments

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterEvent
import com.pointlessapps.mobileusos.databinding.FragmentCalendarBinding
import com.pointlessapps.mobileusos.models.CalendarEvent
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelCommon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.find
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.*

class FragmentCalendar :
	FragmentCoreImpl<FragmentCalendarBinding>(FragmentCalendarBinding::class.java) {

	private val allEvents = mutableListOf<CalendarEvent>()
	private val viewModelCommon by viewModels<ViewModelCommon>()

	private var selectedDay = LocalDate.now()
	private var startDate = MutableLiveData(Calendar.getInstance().time)
	private lateinit var baseFacultyId: String

	override fun getNavigationIcon() = R.drawable.ic_calendar
	override fun getNavigationName() = R.string.calendar

	override fun created() {
		prepareCalendar()
		prepareEventsList()

		startDate.observe(this) { date ->
			ensureBaseFacultyId { id ->
				viewModelCommon.getCalendarByFaculties(listOf(id), date)
					.observe(this) { (list) ->
						allEvents.addAll(list.filter { event ->
							allEvents.find { it.id == event.id } == null
						})
						updateEventList()
						binding().calendar.notifyCalendarChanged()

						binding().pullRefresh.isRefreshing = false
						binding().horizontalProgressBar.isRefreshing = true
					}.onFinished {
						binding().horizontalProgressBar.isRefreshing = false
					}
			}
		}

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		binding().horizontalProgressBar.isRefreshing = true
		startDate.value = startDate.value
	}

	private fun ensureBaseFacultyId(callback: (String) -> Unit) {
		if (::baseFacultyId.isInitialized) {
			callback(baseFacultyId)

			return
		}

		viewModelCommon.getPrimaryFaculty().onOnceCallback { (faculty) ->
			if (faculty != null) {
				baseFacultyId = faculty.id
				GlobalScope.launch(Dispatchers.Main) {
					callback(faculty.id)
				}

				return@onOnceCallback
			}
		}
	}

	private fun prepareEventsList() {
		binding().listEvents.apply {
			adapter = AdapterEvent()
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareCalendar() {
		binding().calendar.dayBinder = object : DayBinder<DayViewContainer> {
			override fun create(view: View) = DayViewContainer(view)
			override fun bind(container: DayViewContainer, day: CalendarDay) {
				container.eventsContainer.apply {
					removeAllViews()
					getEventsFromDay(day.date).forEach {
						addView(View(requireContext()).apply {
							layoutParams = ViewGroup.LayoutParams(6.dp, 6.dp)
							setBackgroundResource(R.drawable.ic_circle)
							backgroundTintList =
								ColorStateList.valueOf(it.getColor(requireContext()))
						})
					}
				}

				container.dayNumber.apply {
					setTextColor(
						if (day.date.dayOfWeek == DayOfWeek.SUNDAY) {
							ContextCompat.getColor(requireContext(), R.color.colorTextRed)
						} else {
							requireContext().themeColor(R.attr.colorTextPrimary)
						}
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

						binding().calendar.notifyCalendarChanged()
					}
				}

				container.border.isVisible =
					day.date.dayOfYear == selectedDay.dayOfYear && day.date.year == selectedDay.year
			}
		}

		binding().calendar.monthScrollListener = { month ->
			startDate.value = Calendar.getInstance().apply {
				set(Calendar.YEAR, month.year)
				set(Calendar.MONTH, month.month)
				set(Calendar.DAY_OF_MONTH, 1)
			}.time
		}

		binding().calendar.monthHeaderBinder =
			object : MonthHeaderFooterBinder<HeaderViewContainer> {
				override fun create(view: View) = HeaderViewContainer(view)
				override fun bind(container: HeaderViewContainer, month: CalendarMonth) {
					container.monthName.text =
						requireContext().resources.getStringArray(R.array.month_names)[month.month - 1]

					container.buttonNext.setOnClickListener {
						binding().calendar.scrollToMonth(month.yearMonth.plusMonths(1))
					}

					container.buttonPrevious.setOnClickListener {
						binding().calendar.scrollToMonth(month.yearMonth.minusMonths(1))
					}
				}
			}

		val currentMonth = YearMonth.now()
		val firstMonth = currentMonth.minusMonths(10)
		val lastMonth = currentMonth.plusMonths(10)
		binding().calendar.setup(firstMonth, lastMonth, DayOfWeek.MONDAY)
		binding().calendar.scrollToMonth(YearMonth.now())
	}

	private fun updateEventList() {
		val events = getEventsFromDay(selectedDay)
		(binding().listEvents.adapter as? AdapterEvent)?.update(events)
		binding().labelEvents.isVisible = events.isNotEmpty()
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
