package com.pointlessapps.mobileusos.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

import com.pointlessapps.mobileusos.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class WeekView extends View {

	public static final int MIN_NUMBER_OF_VISIBLE_DAYS = 3;
	public static final int MAX_NUMBER_OF_VISIBLE_DAYS = 7;
	private final float startHourHeight = 200;
	private final float startDayWidth = 200;
	private final int scrollDuration = 300;
	private final int snappingThreshold = 2;
	private EmptyViewClickListener emptyViewClickListener;
	private DateTimeInterpreter dateTimeInterpreter;
	private MonthChangeListener monthChangeListener;
	private EventClickListener eventClickListener;
	private ScaleGestureDetector scaleDetector;
	private GestureDetector gestureDetector;
	private ScrollListener scrollListener;
	private OverScroller scroller;
	private Calendar today;
	private Map<String, List<WeekViewEvent>> eventsByMonths;
	private List<EventRect> eventRects;
	private float offsetX;
	private float offsetY;
	private float maxOffsetY;
	private float minScale = 0.75f;
	private float maxScale = 2f;
	private float scaleX = 1;
	private float scaleY = 1;
	private int minHourToScroll = 8;
	private int maxHourToScroll = 16;
	private float hourHeight = startHourHeight;
	private float dayWidth = startDayWidth;
	private float goToTodayRadius = WeekViewUtil.dpToPx(getContext(), 24);
	private float dp5 = WeekViewUtil.dpToPx(getContext(), 5);
	private boolean currentTimeLineShowed;
	private RectF currentTimeLinePos;

	private Calendar firstVisibleDay;
	private int numberOfVisibleDays;
	private boolean goToTodayVisible;
	private boolean snappingEnabled;
	private boolean isTouching;

	private boolean refreshDatasetPending = false;

	@ColorInt
	private int headerColor;
	@ColorInt
	private int headerTextColor;
	@ColorInt
	private int todayTextColor;
	@ColorInt
	private int pastBackgroundColor;
	@ColorInt
	private int futureBackgroundColor;
	@ColorInt
	private int weekendBackgroundColor;
	@ColorInt
	private int dividerLineColor;
	@ColorInt
	private int currentTimeLineColor;
	@ColorInt
	private int defaultEventColor;
	@ColorInt
	private int eventTextColor;
	@ColorInt
	private int goToTodayArrowColor;
	@ColorInt
	private int goToTodayBgColor;
	private Typeface headerTextTypeface;
	private Typeface eventTextTypeface;
	private Padding eventTextPadding;
	private Padding headerPadding;
	private float currentTimeLineThickness;
	private float overlappingEventsGap;
	private float eventCornerRadius;
	private float headerTextSize;
	private float eventTextSize;
	private float headerWidth;
	private float headerHeight;

	@IntRange(from = 0, to = 24)
	private int startHour;
	@IntRange(from = 0, to = 24)
	private int endHour;

	private Paint headerPaint;
	private Paint headerTextPaint;
	private Paint dividerLinePaint;
	private Paint backgroundPaint;
	private Paint currentTimeLinePaint;
	private Paint eventPaint;
	private Paint shadowPaint;
	private Paint goToTodayPaint;
	private TextPaint eventTextPaint;

	private LinearGradient dayHeaderShadow;
	private LinearGradient hourHeaderShadow;

	private Drawable commentDrawable;

	public WeekView(Context context) {
		this(context, null);
	}

	public WeekView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WeekView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0);

		eventTextPadding = new Padding();
		headerPadding = new Padding();
		currentTimeLinePos = new RectF();

		try {
			headerTextSize = a.getDimension(R.styleable.WeekView_headerTextSize, getResources().getDimension(R.dimen.default_header_text_size));
			headerTextTypeface = ResourcesCompat.getFont(context, a.getResourceId(R.styleable.WeekView_headerTextTypeface, R.font.montserrat));
			eventTextSize = a.getDimension(R.styleable.WeekView_eventTextSize, getResources().getDimension(R.dimen.default_event_text_size));
			eventTextTypeface = ResourcesCompat.getFont(context, a.getResourceId(R.styleable.WeekView_eventTextTypeface, R.font.montserrat));
			headerPadding.left = a.getDimension(R.styleable.WeekView_headerPaddingLeft, dp5);
			headerPadding.right = a.getDimension(R.styleable.WeekView_headerPaddingRight, dp5);
			headerPadding.top = a.getDimension(R.styleable.WeekView_headerPaddingTop, dp5 * 3);
			headerPadding.bottom = a.getDimension(R.styleable.WeekView_headerPaddingBottom, dp5 * 3);
			headerTextColor = a.getColor(R.styleable.WeekView_headerTextColor, getResources().getColor(R.color.headerTextColor));
			headerColor = a.getColor(R.styleable.WeekView_headerBackgroundColor, getResources().getColor(R.color.headerColor));
			dividerLineColor = a.getColor(R.styleable.WeekView_dividerLineColor, getResources().getColor(R.color.horizontalLineColor));
			todayTextColor = a.getColor(R.styleable.WeekView_todayHeaderTextColor, getResources().getColor(R.color.todayTextColor));
			eventTextColor = a.getColor(R.styleable.WeekView_defaultEventTextColor, getResources().getColor(R.color.defaultEventTextColor));
			eventTextPadding.left = eventTextPadding.right = eventTextPadding.top = eventTextPadding.bottom = a.getDimension(R.styleable.WeekView_eventPadding, dp5);
			overlappingEventsGap = a.getDimension(R.styleable.WeekView_overlappingEventGap, 5);
			eventCornerRadius = a.getDimension(R.styleable.WeekView_eventCornerRadius, 10);
			futureBackgroundColor = a.getColor(R.styleable.WeekView_futureBackgroundColor, getResources().getColor(R.color.futureBackgroundColor));
			pastBackgroundColor = a.getColor(R.styleable.WeekView_pastBackgroundColor, getResources().getColor(R.color.pastBackgroundColor));
			weekendBackgroundColor = a.getColor(R.styleable.WeekView_weekendBackgroundColor, getResources().getColor(R.color.weekendBackgroundColor));
			currentTimeLineColor = a.getColor(R.styleable.WeekView_nowLineColor, getResources().getColor(R.color.currentTimeLineColor));
			currentTimeLineThickness = a.getDimension(R.styleable.WeekView_nowLineThickness, dp5 / 5);
			defaultEventColor = a.getColor(R.styleable.WeekView_defaultEventColor, getResources().getColor(R.color.defaultEventColor));
			startHour = a.getInteger(R.styleable.WeekView_startHour, 0);
			endHour = a.getInteger(R.styleable.WeekView_endHour, 24);
			snappingEnabled = a.getBoolean(R.styleable.WeekView_daySnapping, true);
			numberOfVisibleDays = a.getInteger(R.styleable.WeekView_noOfVisibleDays, 5);
			goToTodayVisible = a.getBoolean(R.styleable.WeekView_goToTodayVisible, true);
			goToTodayArrowColor = a.getColor(R.styleable.WeekView_goToTodayArrowColor, getResources().getColor(R.color.headerTextColor));
			goToTodayBgColor = a.getColor(R.styleable.WeekView_goToTodayBgColor, getResources().getColor(R.color.headerColor));
		} catch (Exception ignored) {
		}

		a.recycle();

		dateTimeInterpreter = new DateTimeInterpreter() {
			private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
			private final SimpleDateFormat weekdayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

			@Override
			public String interpretDate(Calendar date) {
				return dateFormat.format(date.getTime());
			}

			@Override
			public String interpretWeekday(Calendar date) {
				return weekdayFormat.format(date.getTime());
			}

			@Override
			public String interpretTime(int hour) {
				return (hour < 10 ? "0" : "") + hour;
			}
		};

		today = Calendar.getInstance();
		scroller = new OverScroller(context, new LinearOutSlowInInterpolator());

		eventsByMonths = new HashMap<>();
		eventRects = new ArrayList<>();

		headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		headerPaint.setColor(headerColor);

		headerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		headerTextPaint.setColor(headerTextColor);
		headerTextPaint.setTextSize(headerTextSize);
		headerTextPaint.setTypeface(headerTextTypeface);

		dividerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		dividerLinePaint.setColor(dividerLineColor);

		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setColor(futureBackgroundColor);

		currentTimeLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		currentTimeLinePaint.setColor(currentTimeLineColor);

		eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		eventPaint.setColor(defaultEventColor);

		eventTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		eventTextPaint.setColor(eventTextColor);
		eventTextPaint.setTypeface(Typeface.create(eventTextTypeface, Typeface.BOLD));
		eventTextPaint.setTextSize(eventTextSize);

		Rect textBounds = new Rect();
		String time = dateTimeInterpreter.interpretTime(0);
		headerTextPaint.getTextBounds(time, 0, time.length(), textBounds);
		headerWidth = textBounds.width() + headerPadding.left + headerPadding.right;
		headerHeight = textBounds.height() * 2.5f + headerPadding.top + headerPadding.bottom;

		gestureDetector = new GestureDetector(context, new WeekViewGestureDetector());
		scaleDetector = new ScaleGestureDetector(context, new WeekViewScaleDetector());

		goToTodayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		goToTodayPaint.setColor(goToTodayBgColor);
		goToTodayPaint.setStrokeWidth(WeekViewUtil.dpToPx(getContext(), 2));
		goToTodayPaint.setStrokeCap(Paint.Cap.ROUND);

		shadowPaint = new Paint(Paint.DITHER_FLAG);
		shadowPaint.setDither(true);

		dayHeaderShadow = new LinearGradient(
				getRight() * 0.5f,
				headerHeight - 5f,
				getRight() * 0.5f,
				headerHeight + 10f,
				Color.argb(38, 0, 0, 0),
				Color.argb(0, 0, 0, 0),
				Shader.TileMode.CLAMP
		);

		hourHeaderShadow = new LinearGradient(
				headerWidth - 5f,
				getBottom() * 0.5f,
				headerWidth + 10f,
				getBottom() * 0.5f,
				Color.argb(38, 0, 0, 0),
				Color.argb(0, 0, 0, 0),
				Shader.TileMode.CLAMP
		);

		commentDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_note, null);
		assert commentDrawable != null;
		commentDrawable.setAlpha(150);

		post(() -> {
			computeMaxOffset();
			computeNumberOfVisibleDays(numberOfVisibleDays);
			computeScale();
			updateFirstVisibleDay();
			scrollToHour(Math.max(minHourToScroll, Math.min(maxHourToScroll, today.get(Calendar.HOUR_OF_DAY))));
			scrollToToday();
		});
	}

	private int getStartHour() {
		if (startHour < 0) {
			return today.get(Calendar.HOUR_OF_DAY) + startHour;
		}

		return startHour;
	}

	public void setStartHour(int startHour) {
		this.startHour = startHour;
		computeMaxOffset();
		invalidate();
	}

	private int getEndHour() {
		if (endHour < 0) {
			return today.get(Calendar.HOUR_OF_DAY) - endHour;
		}

		return endHour;
	}

	public void setEndHour(int endHour) {
		this.endHour = endHour;
		computeMaxOffset();
		invalidate();
	}

	private void computeMaxOffset() {
		if (startHour < 0 || endHour < 0) {
			// Here we have to calculate differences between current time and provided deltas
			maxOffsetY = -(startHour + endHour) * hourHeight - (getMeasuredHeight() - headerHeight) + headerTextSize;

			return;
		}
		maxOffsetY = (endHour - startHour) * hourHeight - (getMeasuredHeight() - headerHeight) + headerTextSize;
	}

	private void computeNumberOfVisibleDays(int numberOfVisibleDays) {
		if (numberOfVisibleDays == -1) {
			this.numberOfVisibleDays = (int) Math.ceil((getMeasuredWidth() - headerWidth) / dayWidth);
		} else {
			this.numberOfVisibleDays = numberOfVisibleDays;
			scaleX = ((getMeasuredWidth() - headerWidth) / numberOfVisibleDays) / startDayWidth;
			dayWidth = startDayWidth * scaleX;
			invalidate();
		}
	}

	private void computeScale() {
		maxScale = ((getMeasuredWidth() - headerWidth) / MIN_NUMBER_OF_VISIBLE_DAYS) / startDayWidth;
		minScale = ((getMeasuredWidth() - headerWidth) / MAX_NUMBER_OF_VISIBLE_DAYS) / startDayWidth;

		if (dateTimeInterpreter != null) {
			float textLength = headerTextPaint.measureText(dateTimeInterpreter.interpretDate(today));
			if (minScale * startDayWidth < textLength + headerPadding.left + headerPadding.right) {
				minScale = (textLength + headerPadding.left + headerPadding.right) / startDayWidth;
			}
		}
	}

	private void computePositionOfEvents() {
		// Make "collision groups" for all events that collide with others.
		List<List<EventRect>> collisionGroups = new ArrayList<>();
		for (EventRect eventRect : eventRects) {
			boolean isPlaced = false;

			outerLoop:
			for (List<EventRect> collisionGroup : collisionGroups) {
				for (EventRect groupEvent : collisionGroup) {
					if (groupEvent.event.isCollidingWith(eventRect.event)) {
						collisionGroup.add(eventRect);
						isPlaced = true;
						break outerLoop;
					}
				}
			}

			if (!isPlaced) {
				List<EventRect> newGroup = new ArrayList<>();
				newGroup.add(eventRect);
				collisionGroups.add(newGroup);
			}
		}

		for (List<EventRect> collisionGroup : collisionGroups) {
			expandEventsToMaxWidth(collisionGroup);
		}
	}

	private void computeEventsRects() {
		if (eventRects == null) {
			eventRects = new ArrayList<>();
		} else {
			eventRects.clear();
		}

		List<WeekViewEvent> allEvents = new ArrayList<>();
		for (List<WeekViewEvent> events : eventsByMonths.values()) {
			allEvents.addAll(events);
		}

		Collections.sort(allEvents, (event1, event2) -> {
			int comparator = event1.getStartTime().compareTo(event2.getStartTime());
			return comparator == 0 ? event1.getEndTime().compareTo(event2.getEndTime()) : comparator;
		});

		for (WeekViewEvent event : allEvents) {
			eventRects.add(new EventRect(event));
		}

		computePositionOfEvents();
	}

	private void expandEventsToMaxWidth(List<EventRect> collisionGroup) {
		// Expand the events to maximum possible width.
		List<List<EventRect>> columns = new ArrayList<>();
		columns.add(new ArrayList<>());
		for (EventRect eventRect : collisionGroup) {
			boolean isPlaced = false;
			for (List<EventRect> column : columns) {
				if (column.size() == 0) {
					column.add(eventRect);
					isPlaced = true;
				} else if (!eventRect.event.isCollidingWith(column.get(column.size() - 1).event)) {
					column.add(eventRect);
					isPlaced = true;
					break;
				}
			}
			if (!isPlaced) {
				List<EventRect> newColumn = new ArrayList<>();
				newColumn.add(eventRect);
				columns.add(newColumn);
			}
		}


		// Calculate left and right position for all the events.
		// Get the maxRowCount by looking in all columns.
		int maxRowCount = 0;
		for (List<EventRect> column : columns) {
			maxRowCount = Math.max(maxRowCount, column.size());
		}
		for (int i = 0; i < maxRowCount; i++) {
			// Set the left and right values of the event.
			float j = 0;
			for (List<EventRect> column : columns) {
				if (column.size() >= i + 1) {
					EventRect eventRect = column.get(i);
					eventRect.width = 1f / columns.size();
					eventRect.left = j / columns.size();
				}
				j++;
			}
		}
	}

	private void updateOffsets() {
		if (scroller.computeScrollOffset()) {
			offsetX = scroller.getCurrX();
			offsetY = scroller.getCurrY();
			invalidate();
		}
		if (snappingEnabled && !isTouching) {
			float wantedOffset = Math.round(offsetX / dayWidth) * dayWidth;
			int diff = (int) Math.ceil(offsetX - wantedOffset);
			if (Math.abs(diff) >= snappingThreshold) {
				if (scroller.isFinished()) {
					scroller.startScroll((int) offsetX, (int) offsetY, -diff, 0, scrollDuration);
				}
				invalidate();
			}
		}
	}

	private void updateFirstVisibleDay() {
		Calendar oldFirstVisibleDay = firstVisibleDay;

		int dayDiff = (int) Math.ceil(offsetX / dayWidth);
		firstVisibleDay = ((Calendar) today.clone());
		firstVisibleDay.add(Calendar.DAY_OF_MONTH, -dayDiff);

		if (oldFirstVisibleDay == null || !WeekViewUtil.isSameDay(firstVisibleDay, oldFirstVisibleDay)) {
			if (scrollListener != null) {
				scrollListener.onFirstVisibleDayChanged(firstVisibleDay, oldFirstVisibleDay);
			}

//			Check if month has changed and if so update data set
			if (oldFirstVisibleDay == null || !WeekViewUtil.isSameMonth(firstVisibleDay, oldFirstVisibleDay)) {
				refreshDataset();
			}
		}
	}

	private void updateDataset() {
		if (firstVisibleDay == null || monthChangeListener == null) {
			return;
		}

		Calendar temp = (Calendar) firstVisibleDay.clone();
		List<? extends WeekViewEvent> previousMonthEvents, currentMonthEvents, nextMonthsEvents;
		String prevMonthKey, currentMonthKey, nextMonthKey;

		currentMonthEvents = monthChangeListener.onMonthChange(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH));
		currentMonthKey = WeekViewUtil.getMonthKey(temp);

		temp.add(Calendar.MONTH, -1);
		previousMonthEvents = monthChangeListener.onMonthChange(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH));
		prevMonthKey = WeekViewUtil.getMonthKey(temp);

		temp.add(Calendar.MONTH, 2);
		nextMonthsEvents = monthChangeListener.onMonthChange(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH));
		nextMonthKey = WeekViewUtil.getMonthKey(temp);

//		Remove all months except these three
		Set<String> keys = eventsByMonths.keySet();
		List<String> keysToRemove = new ArrayList<>();
		for (String key : keys) {
			if (!key.equals(prevMonthKey) && !key.equals(currentMonthKey) && !key.equals(nextMonthKey)) {
				keysToRemove.add(key);
			}
		}

		for (String key : keysToRemove) {
			eventsByMonths.remove(key);
		}

//		Put all three months into the map
		eventsByMonths.put(prevMonthKey, new ArrayList<>(previousMonthEvents));
		eventsByMonths.put(currentMonthKey, new ArrayList<>(currentMonthEvents));
		eventsByMonths.put(nextMonthKey, new ArrayList<>(nextMonthsEvents));

		computeEventsRects();
		invalidate();
	}

	public void refreshDataset() {
		refreshDatasetPending = true;
		invalidate();
	}

	public void scrollToHour(@IntRange(from = 0, to = 24) int hour) {
		hour = Math.max(getStartHour(), Math.min(getEndHour(), hour)) - (getStartHour());

		offsetY = Math.min(0, Math.max(-maxOffsetY, -(hour * hourHeight - (getMeasuredHeight() - headerHeight) / 2f + headerTextSize)));
		invalidate();
	}

	public void scrollToDate(@NotNull Calendar date) {
		long dayDiff = Math.round((firstVisibleDay.getTimeInMillis() - date.getTimeInMillis()) / (float) TimeUnit.DAYS.toMillis(1));
		scroller.startScroll((int) offsetX, (int) offsetY, (int) (dayDiff * dayWidth), 0, scrollDuration);
		invalidate();
	}

	public void scrollToToday() {
		scrollToDate(today);
	}

	public void setDateTimeInterpreter(@NonNull DateTimeInterpreter dateTimeInterpreter) {
		this.dateTimeInterpreter = dateTimeInterpreter;
	}

	public void setMonthChangeListener(@NonNull MonthChangeListener monthChangeListener) {
		this.monthChangeListener = monthChangeListener;
	}

	public void setScrollListener(@NonNull ScrollListener scrollListener) {
		this.scrollListener = scrollListener;
	}

	public void setEventClickListener(EventClickListener eventClickListener) {
		this.eventClickListener = eventClickListener;
	}

	public void setEmptyViewClickListener(EmptyViewClickListener emptyViewClickListener) {
		this.emptyViewClickListener = emptyViewClickListener;
	}

	public void setEventTextSize(int eventTextSize) {
		this.eventTextSize = eventTextSize;
		eventTextPaint.setTextSize(WeekViewUtil.spToPx(getContext(), eventTextSize));
		invalidate();
	}

	public void setVisibleDays(int defaultVisibleDays) {
		numberOfVisibleDays = defaultVisibleDays;
		computeNumberOfVisibleDays(numberOfVisibleDays);
		invalidate();
	}

	public void setHourDelta(int deltaNegative, int deltaPositive) {
		int hour = today.get(Calendar.HOUR_OF_DAY);
		int negativeDiff = deltaNegative - Math.min(deltaNegative, hour);
		int positiveDiff = deltaPositive - Math.min(deltaPositive, 24 - hour);
		startHour = -Math.min(deltaNegative, hour) - Math.max(positiveDiff, 0);
		endHour = -Math.min(deltaPositive, 24 - hour) - Math.max(negativeDiff, 0);
		computeMaxOffset();
		invalidate();
	}

	public void scaleToFit(float width, float height) {
		dayWidth = (width - headerWidth) / numberOfVisibleDays;
		hourHeight = (height - headerHeight) / (getEndHour() - getStartHour());
		computeNumberOfVisibleDays(-1);
		computeMaxOffset();

		invalidate();
	}

	public void setToday(Calendar today) {
		this.today = today;
		updateFirstVisibleDay();
		scrollToToday();
	}

	public void setFutureBackgroundColor(@ColorInt int color) {
		this.futureBackgroundColor = color;
	}

	public void setPastBackgroundColor(@ColorInt int color) {
		this.pastBackgroundColor = color;
	}

	public void setWeekendBackgroundColor(@ColorInt int color) {
		this.weekendBackgroundColor = color;
	}

	public void setCurrentTimeLineColor(@ColorInt int color) {
		this.currentTimeLineColor = color;
		currentTimeLinePaint.setColor(currentTimeLineColor);
	}

	public void setTodayTextColor(@ColorInt int color) {
		this.todayTextColor = color;
	}

	public void setHeaderTextColor(@ColorInt int color) {
		this.headerTextColor = color;
		headerTextPaint.setColor(headerTextColor);
	}

	public void setHeaderColor(@ColorInt int color) {
		this.headerColor = color;
		headerPaint.setColor(headerColor);
	}

	public void setDividerLineColor(@ColorInt int color) {
		this.dividerLineColor = color;
		dividerLinePaint.setColor(dividerLineColor);
	}

	public void setSnappingEnabled(boolean snappingEnabled) {
		this.snappingEnabled = snappingEnabled;
	}

	public Calendar getFirstVisibleDay() {
		return firstVisibleDay;
	}

	private Calendar getTimeFromPoint(float x, float y) {
		Calendar selectedTime = (Calendar) firstVisibleDay.clone();
		int dayDiff = (int) Math.floor((x - headerWidth) / dayWidth);
		selectedTime.add(Calendar.DAY_OF_MONTH, dayDiff);

		int hourFromBeginning = (int) Math.floor((y - headerHeight - offsetY) / hourHeight);
		int minute = (int) ((y - headerHeight - offsetY - hourFromBeginning * hourHeight) / hourHeight * 60);
		int hour = hourFromBeginning + getStartHour();
		selectedTime.set(Calendar.HOUR_OF_DAY, hour);
		selectedTime.set(Calendar.MINUTE, minute);

		return selectedTime;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		updateOffsets();
		updateFirstVisibleDay();

		if (refreshDatasetPending) {
			updateDataset();
			refreshDatasetPending = false;
		}

		drawDaysColors(canvas);
		drawHorizontalLines(canvas);
		drawVerticalLines(canvas);
		drawHourHeader(canvas);
		drawDayHeader(canvas);
		drawEvents(canvas);
		drawCurrentTimeLine(canvas);
		drawGoToToday(canvas);
	}

	private void drawDaysColors(Canvas canvas) {
		float dayOffsetX = offsetX - (int) Math.ceil(offsetX / dayWidth) * dayWidth;

		currentTimeLineShowed = false;

		Calendar currentDay = (Calendar) firstVisibleDay.clone();
		for (int i = 0; i <= numberOfVisibleDays; i++, currentDay.add(Calendar.DAY_OF_MONTH, 1)) {
			int backgroundColor = WeekViewUtil.isWeekday(currentDay) ? weekendBackgroundColor : futureBackgroundColor;

			if (WeekViewUtil.isSameDay(currentDay, today)) {
				float x = i * dayWidth + headerWidth + dayOffsetX;

				float fraction = WeekViewUtil.getFractionOfDay(today);
				float maxHeight = hourHeight * 24;
				float realY = fraction * maxHeight - getStartHour() * hourHeight; //Compensate for selected smaller portion of a day to show
				float y = realY + offsetY + headerHeight + headerPadding.top;

				backgroundPaint.setColor(pastBackgroundColor);
				canvas.drawRect(x, 0, x + dayWidth, y, backgroundPaint);

				backgroundPaint.setColor(backgroundColor);
				canvas.drawRect(x, y, x + dayWidth, getBottom(), backgroundPaint);

				currentTimeLinePos.set(x, y - currentTimeLineThickness / 2f, x + dayWidth, y + currentTimeLineThickness / 2f);
				currentTimeLineShowed = true;
			} else {
				if (currentDay.before(today)) {
					backgroundPaint.setColor(pastBackgroundColor);
				} else {
					backgroundPaint.setColor(backgroundColor);
				}

				float x = i * dayWidth + headerWidth + dayOffsetX;

				canvas.drawRect(x, 0, x + dayWidth, getBottom(), backgroundPaint);
			}
		}
	}

	private void drawHorizontalLines(Canvas canvas) {
		for (int i = getStartHour(), index = 0; i < getEndHour(); i++, index++) {

//			Check if it's visible on the screen
			float y = headerPadding.top + hourHeight * index + headerHeight + offsetY;
			if (y - headerTextSize < 0 || y - headerTextSize > getBottom()) {
				continue;
			}

			canvas.drawLine(headerWidth, y, getRight(), y, dividerLinePaint);
		}
	}

	private void drawVerticalLines(Canvas canvas) {
		for (int i = 0; i <= numberOfVisibleDays; i++) {

//			Check if it's visible on the screen
			float x = dayWidth * i + headerWidth + (offsetX - (int) (offsetX / dayWidth) * dayWidth);
			if (x - headerWidth < 0 || x > getRight()) {
				continue;
			}

			canvas.drawLine(x, headerHeight, x, getBottom(), dividerLinePaint);
		}
	}

	/**
	 * Draws horizontal header with dates of column's corresponding days
	 */
	private void drawDayHeader(Canvas canvas) {
		shadowPaint.setShader(dayHeaderShadow);
		canvas.drawRect(headerWidth, headerHeight - 5f, getRight(), headerHeight + 10f, shadowPaint);

		canvas.drawRect(0, 0, getRight(), headerHeight, headerPaint);

		canvas.save();
		canvas.clipRect(headerWidth, 0, getRight(), headerHeight);

		float dayOffsetX = offsetX - (int) Math.ceil(offsetX / dayWidth) * dayWidth;

		Calendar currentDay = (Calendar) firstVisibleDay.clone();
		for (int i = 0; i <= numberOfVisibleDays; i++, currentDay.add(Calendar.DAY_OF_MONTH, 1)) {
			if (dateTimeInterpreter == null) {
				throw new RuntimeException("DateTimeInterpreter cannot be null!");
			}

			String day = dateTimeInterpreter.interpretDate(currentDay);

			float x = i * dayWidth + (dayWidth - headerTextPaint.measureText(day)) / 2 + headerWidth + dayOffsetX;

			if (WeekViewUtil.isSameDay(currentDay, today)) {
				headerTextPaint.setColor(todayTextColor);
			} else {
				headerTextPaint.setColor(headerTextColor);
			}

			canvas.drawText(day, x, headerPadding.top + headerTextSize, headerTextPaint);

			headerTextPaint.setColor(dividerLineColor);
			headerTextPaint.setTextSize(headerTextSize * 0.8f);

			String weekday = dateTimeInterpreter.interpretWeekday(currentDay);
			x = i * dayWidth + (dayWidth - headerTextPaint.measureText(weekday)) / 2 + headerWidth + dayOffsetX;
			canvas.drawText(weekday, x, headerPadding.top + headerTextSize * 2.2f, headerTextPaint);

			headerTextPaint.setTextSize(headerTextSize);
			headerTextPaint.setColor(headerTextColor);
		}
		canvas.restore();
	}

	/**
	 * Draws vertical header with hours
	 */
	private void drawHourHeader(Canvas canvas) {
		shadowPaint.setShader(hourHeaderShadow);
		canvas.drawRect(headerWidth - 5f, 0, headerWidth + 10f, getBottom(), shadowPaint);

		canvas.drawRect(0, 0, headerWidth, getBottom(), headerPaint);

		canvas.save();
		canvas.clipRect(0, headerHeight, headerWidth, getBottom());

//		Draw hours labels
		for (int i = getStartHour(), index = 0; i <= getEndHour(); i++, index++) {
			if (dateTimeInterpreter == null) {
				throw new RuntimeException("DateTimeInterpreter cannot be null!");
			}

			String hour = dateTimeInterpreter.interpretTime(i);

//			Check if it's visible on the screen
			float y = headerPadding.top + hourHeight * index + headerHeight + offsetY;
			if (y - headerTextSize < 0 || y - headerTextSize > getBottom()) {
				continue;
			}

			canvas.drawText(hour, (headerWidth - headerTextPaint.measureText(hour)) * 0.5f, y, headerTextPaint);
		}
		canvas.restore();
	}

	private void drawEvents(Canvas canvas) {
		float dayOffsetX = offsetX - (int) Math.ceil(offsetX / dayWidth) * dayWidth;

		canvas.save();
		canvas.clipRect(headerWidth, headerHeight, getRight(), getBottom());
		Calendar currentDay = (Calendar) firstVisibleDay.clone();
		for (int i = 0; i <= numberOfVisibleDays; i++, currentDay.add(Calendar.DAY_OF_MONTH, 1)) {
			float x = i * dayWidth + headerWidth + dayOffsetX;
			drawDayEvents(canvas, currentDay, x);
		}
		canvas.restore();
	}

	private void drawDayEvents(Canvas canvas, Calendar day, float x) {
		float maxHeight = hourHeight * 24;
		for (EventRect eventRect : eventRects) {
			if (WeekViewUtil.isSameDay(eventRect.event.getStartTime(), day)) {
				float startFraction = WeekViewUtil.getFractionOfDay(eventRect.event.getStartTime());
				float startRealY = startFraction * maxHeight - getStartHour() * hourHeight; //Compensate for selected smaller portion of a day to show
				float endFraction = WeekViewUtil.getFractionOfDay(eventRect.event.getEndTime());
				float endRealY = endFraction * maxHeight - getStartHour() * hourHeight;

				float left = x + dayWidth * eventRect.left + overlappingEventsGap;
				float top = startRealY + offsetY + headerHeight + headerPadding.top;
				float right = left + dayWidth * eventRect.width - overlappingEventsGap;
				float bottom = endRealY + offsetY + headerHeight + headerPadding.top;

				eventRect.rectF = new RectF(left, top, right, bottom);

				eventPaint.setColor(eventRect.event.getColor() == 0 ? defaultEventColor : eventRect.event.getColor());
				canvas.drawRoundRect(eventRect.rectF, eventCornerRadius, eventCornerRadius, eventPaint);

				if (eventRect.event.hasOutline()) {
					eventPaint.setColor(getContext().getResources().getColor(R.color.colorAccent));
					canvas.drawRoundRect(left, top, right, top + dp5 * 0.5f, eventCornerRadius, eventCornerRadius, eventPaint);
				}

				if (eventRect.event.comment != null && !eventRect.event.comment.isEmpty()) {
					float maxWidth = Math.min(eventRect.rectF.width(), eventRect.rectF.height()) - 50;
					float horizontalOffset = (eventRect.rectF.width() - maxWidth) * 0.5f;
					float verticalOffset = (eventRect.rectF.height() - maxWidth) * 0.5f;
					commentDrawable.setBounds(
							(int) (left + horizontalOffset),
							(int) (top + verticalOffset),
							(int) (right - horizontalOffset),
							(int) (bottom - verticalOffset)
					);
					commentDrawable.draw(canvas);
				}

				drawEventTitle(eventRect.event, eventRect.rectF, canvas, eventRect.rectF.top, eventRect.rectF.left);
			}
		}
	}

	private void drawEventTitle(WeekViewEvent event, RectF rect, Canvas canvas, float originalTop, float originalLeft) {
		int availableHeight = (int) (rect.bottom - originalTop - eventTextPadding.left - eventTextPadding.right);
		int availableWidth = (int) (rect.right - originalLeft - eventTextPadding.top - eventTextPadding.bottom);

		if (availableWidth <= 0 || availableHeight <= 0) {
			return;
		}

		// Get text dimensions.
		StaticLayout textLayout = new StaticLayout(event.getName(), eventTextPaint, availableWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			textLayout = StaticLayout.Builder.obtain(event.getName(), 0, event.getName().length(), eventTextPaint, availableWidth).build();
		}

		int lineHeight = textLayout.getHeight() / textLayout.getLineCount();

		if (availableHeight >= lineHeight) {
			// Calculate available number of line counts.
			int availableLineCount = availableHeight / lineHeight;
			do {
				// Ellipsize text to fit into event rect.
				textLayout = new StaticLayout(TextUtils.ellipsize(event.getName(), eventTextPaint, availableLineCount * availableWidth, TextUtils.TruncateAt.END),
						eventTextPaint, (int) (rect.right - originalLeft - eventTextPadding.left - eventTextPadding.right), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

				// Reduce line count.
				availableLineCount--;

				// Repeat until text is short enough.
			} while (textLayout.getHeight() > availableHeight);

			// Draw text.
			canvas.save();
			canvas.translate(originalLeft + eventTextPadding.left, originalTop + eventTextPadding.top);
			textLayout.draw(canvas);
			canvas.restore();
		}
	}

	private void drawCurrentTimeLine(Canvas canvas) {
		if (!currentTimeLineShowed) {
			return;
		}

		canvas.save();
		canvas.clipRect(headerWidth, headerHeight, getRight(), getBottom());
		canvas.drawRect(currentTimeLinePos, currentTimeLinePaint);
		canvas.restore();
	}

	private void drawGoToToday(Canvas canvas) {
		if (!goToTodayVisible) {
			return;
		}

		if (WeekViewUtil.isSameDay(firstVisibleDay, today)) {
			return;
		}

		float x = headerWidth + goToTodayRadius * 2f;
		float y = getBottom() - WeekViewUtil.dpToPx(getContext(), 75) - goToTodayRadius;
		float flipped = firstVisibleDay.getTimeInMillis() < today.getTimeInMillis() ? -1 : 1;

		if (flipped == -1) {
			x = getRight() - goToTodayRadius * 2f;
		}

		goToTodayPaint.setColor(goToTodayBgColor);
		goToTodayPaint.setStyle(Paint.Style.FILL);
		canvas.drawCircle(x, y, goToTodayRadius, goToTodayPaint);

		goToTodayPaint.setColor(goToTodayArrowColor);
		goToTodayPaint.setStyle(Paint.Style.STROKE);
		goToTodayPaint.setStrokeWidth(WeekViewUtil.dpToPx(getContext(), 1));
		canvas.drawCircle(x, y, goToTodayRadius, goToTodayPaint);

		goToTodayPaint.setStrokeWidth(WeekViewUtil.dpToPx(getContext(), 2));
		canvas.drawLines(new float[]{
				x + goToTodayRadius * 0.25f * flipped, y - goToTodayRadius * 0.45f,
				x - goToTodayRadius * 0.25f * flipped, y,
				x + goToTodayRadius * 0.25f * flipped, y + goToTodayRadius * 0.45f,
				x - goToTodayRadius * 0.25f * flipped, y,
		}, goToTodayPaint);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		It's necessary for snapping
		if (event.getAction() == MotionEvent.ACTION_UP) {
			invalidate();
			isTouching = false;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			isTouching = true;
		}

		boolean gD = gestureDetector.onTouchEvent(event);
		boolean sD = scaleDetector.onTouchEvent(event);
		return sD || gD;
	}

	public interface DateTimeInterpreter {
		String interpretDate(Calendar date);

		String interpretWeekday(Calendar date);

		String interpretTime(int hour);
	}

	public interface ScrollListener {
		void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, @Nullable Calendar oldFirstVisibleDay);
	}

	public interface EmptyViewClickListener {
		void onEmptyViewClicked(Calendar time);
	}

	public interface EventClickListener {
		void onEventClick(WeekViewEvent event, RectF eventRect);
	}

	public interface MonthChangeListener {
		@NonNull
		List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth);
	}

	public static class WeekViewUtil {

		public static boolean isSameDay(Calendar dayOne, Calendar dayTwo) {
			return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.DAY_OF_YEAR) == dayTwo.get(Calendar.DAY_OF_YEAR);
		}

		static boolean isSameMonth(Calendar dayOne, Calendar dayTwo) {
			return dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR) && dayOne.get(Calendar.MONTH) == dayTwo.get(Calendar.MONTH);
		}

		public static float getFractionOfDay(Calendar day) {
			return getFractionOfDay(day.get(Calendar.HOUR_OF_DAY), day.get(Calendar.MINUTE));
		}

		static float getFractionOfDay(int hour, int minute) {
			return (hour * 60 + minute) / (float) TimeUnit.DAYS.toMinutes(1);
		}

		public static boolean isWeekday(Calendar day) {
			return day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
		}

		static String getMonthKey(Calendar day) {
			return new SimpleDateFormat("dd.MM", Locale.getDefault()).format(day.getTime());
		}

		static int dpToPx(Context context, int dp) {
			if (context != null) {
				return (int) (context.getResources().getDisplayMetrics().density * dp);
			} else {
				return 0;
			}
		}

		static int spToPx(Context context, int sp) {
			if (context != null) {
				return (int) (context.getResources().getDisplayMetrics().scaledDensity * sp);
			} else {
				return 0;
			}
		}
	}

	public static class EventRect {

		public final WeekViewEvent event;
		public RectF rectF;

		@FloatRange(from = 0, to = 1)
		public float width;
		@FloatRange(from = 0, to = 1)
		public float left;

		public EventRect(WeekViewEvent event) {
			this.event = event;
			this.rectF = null;
		}
	}

	public static class Padding {
		public float bottom, top, left, right;
	}

	public static class WeekViewEvent {
		private long mId;
		private Calendar mStartTime;
		private Calendar mEndTime;
		private String mName;
		private int mColor;
		private boolean hasOutline;
		private String comment;

		public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime, String comment) {
			this.mId = id;
			this.mName = name;
			this.mStartTime = startTime;
			this.mEndTime = endTime;
			this.comment = comment;
		}

		public Calendar getStartTime() {
			return mStartTime;
		}

		public void setStartTime(Calendar startTime) {
			this.mStartTime = startTime;
		}

		public Calendar getEndTime() {
			return mEndTime;
		}

		public void setEndTime(Calendar endTime) {
			this.mEndTime = endTime;
		}

		public String getName() {
			return mName;
		}

		public void setName(String name) {
			this.mName = name;
		}

		public int getColor() {
			return mColor;
		}

		public void setColor(int color) {
			this.mColor = color;
		}

		public boolean hasOutline() {
			return hasOutline;
		}

		public void setHasOutline(boolean hasOutline) {
			this.hasOutline = hasOutline;
		}

		public long getId() {
			return mId;
		}

		public void setId(long id) {
			this.mId = id;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			WeekViewEvent that = (WeekViewEvent) o;

			return mId == that.mId;

		}

		@Override
		public int hashCode() {
			return (int) (mId ^ (mId >>> 32));
		}

		public boolean isCollidingWith(WeekViewEvent event) {
			long start1 = getStartTime().getTimeInMillis();
			long end1 = getEndTime().getTimeInMillis();
			long start2 = event.getStartTime().getTimeInMillis();
			long end2 = event.getEndTime().getTimeInMillis();
			return !((start1 >= end2) || (end1 <= start2));
		}
	}

	private class WeekViewGestureDetector extends GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			scroller.forceFinished(true);
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			offsetX -= distanceX;
			offsetY = Math.min(0, Math.max(-maxOffsetY, offsetY - distanceY));
			invalidate();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			scroller.fling((int) offsetX, (int) offsetY, (int) velocityX, (int) velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, -(int) maxOffsetY, 0);
			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// If the tap was on go to today button scroll there
			if (goToTodayVisible) {
				if (!WeekViewUtil.isSameDay(firstVisibleDay, today)) {
					float x = headerWidth + goToTodayRadius * 2f;
					float y = getBottom() - WeekViewUtil.dpToPx(getContext(), 75) - goToTodayRadius;
					float flipped = firstVisibleDay.getTimeInMillis() < today.getTimeInMillis() ? -1 : 1;
					float minX = (flipped == -1 ? getRight() - goToTodayRadius * 2f : x) - goToTodayRadius;
					float maxX = minX + goToTodayRadius * 2f;
					float minY = y - goToTodayRadius;
					float maxY = minY + goToTodayRadius * 2f;

					if (e.getX() >= minX && e.getX() <= maxX && e.getY() >= minY && e.getY() <= maxY) {
						scrollToToday();

						return true;
					}
				}
			}

			// If the tap was on an event then trigger the callback.
			if (eventRects != null && eventClickListener != null) {
				List<EventRect> reversedEventRects = eventRects;
				Collections.reverse(reversedEventRects);
				for (EventRect event : reversedEventRects) {
					if (event.rectF != null && event.rectF.contains(e.getX(), e.getY())) {
						eventClickListener.onEventClick(event.event, event.rectF);
						return true;
					}
				}
			}

			// If the tap was on in an empty space, then trigger the callback.
			if (emptyViewClickListener != null && e.getX() > headerWidth && e.getY() > headerHeight) {
				emptyViewClickListener.onEmptyViewClicked(getTimeFromPoint(e.getX(), e.getY()));
			}

			return true;
		}
	}

	private class WeekViewScaleDetector implements ScaleGestureDetector.OnScaleGestureListener {

		private final float oneWayScaleThresholdX = 100;
		private final float oneWayScaleThresholdY = 200;
		private final int SCALE_HORIZONTAL = 1;
		private final int SCALE_VERTICAL = 1 << 1;
		private final int SCALE_BOTH = 1 << 2;
		private float startPosX;
		private float startPosY;
		private float startScaleX;
		private float startScaleY;
		private float startOffsetX;
		private float startOffsetY;
		private int scaleDirection;

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float sX = (scaleDirection & SCALE_VERTICAL) != SCALE_VERTICAL ? detector.getScaleFactor() : 1;
			float sY = (scaleDirection & SCALE_HORIZONTAL) != SCALE_HORIZONTAL ? detector.getScaleFactor() : 1;
			scaleX = Math.max(minScale, Math.min(maxScale, scaleX * sX));
			scaleY = Math.max(minScale, Math.min(maxScale, scaleY * sY));

			hourHeight = startHourHeight * scaleY;
			dayWidth = startDayWidth * scaleX;

			float diffX = (startPosX - startOffsetX) * (1 - scaleX / startScaleX);
			float diffY = (startPosY - startOffsetY) * (1 - scaleY / startScaleY);

			computeMaxOffset();
			computeNumberOfVisibleDays(-1);

			offsetX = startOffsetX + diffX;
			offsetY = Math.min(0, Math.max(-maxOffsetY, startOffsetY + diffY));
			invalidate();

			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			startPosX = detector.getFocusX();
			startPosY = detector.getFocusY();
			startScaleX = scaleX;
			startScaleY = scaleY;
			startOffsetX = offsetX;
			startOffsetY = offsetY;

			if (detector.getCurrentSpanX() < oneWayScaleThresholdX) {
				scaleDirection = SCALE_VERTICAL;
			} else if (detector.getCurrentSpanY() < oneWayScaleThresholdY) {
				scaleDirection = SCALE_HORIZONTAL;
			} else {
				scaleDirection = SCALE_BOTH;
			}

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {

		}
	}
}
