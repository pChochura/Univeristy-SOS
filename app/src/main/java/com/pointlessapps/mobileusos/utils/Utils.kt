package com.pointlessapps.mobileusos.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.text.Html
import android.text.Spanned
import android.widget.AdapterView
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.use
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.activities.ActivityWidgetSettings
import com.pointlessapps.mobileusos.adapters.AdapterAutocomplete
import com.pointlessapps.mobileusos.databinding.*
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.models.*
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.repositories.RepositoryUser
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Utils {

	fun monthKey(month: Int, year: Int) =
		"%02d.%d".format(month, year)

	fun dayKey(day: Int, month: Int, year: Int) =
		"%02d.%02d.%d".format(day, month, year)

	fun getScreenSize() =
		Point(
			Resources.getSystem().displayMetrics.widthPixels,
			Resources.getSystem().displayMetrics.heightPixels
		)

	fun getColorByClassType(classType: String?) =
		when (classType?.toLowerCase(Locale.getDefault())) {
			"wyk" -> Color.parseColor("#FF80CBC4")
			"ćw" -> Color.parseColor("#FFF06292")
			"lab" -> Color.parseColor("#FFFF9E80")
			"wf" -> Color.parseColor("#FF81C784")
			else -> Color.parseColor("#FFAED581")
		}

	private fun calendarIntent(context: Context, event: CourseEvent) {
		val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
			.setData(CalendarContract.Events.CONTENT_URI)
			.putExtra(CalendarContract.Events.TITLE, event.name())
			.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
			.putExtra(
				CalendarContract.EXTRA_EVENT_BEGIN_TIME,
				event.startTime.time
			)
			.putExtra(
				CalendarContract.EXTRA_EVENT_END_TIME,
				event.endTime?.time
			)
			.putExtra(
				CalendarContract.Events.EVENT_LOCATION,
				if (event.buildingName != null) "${event.buildingName.toString()}, ${event.roomNumber}"
				else event.roomNumber
			)
			.putExtra(
				CalendarContract.Events.DESCRIPTION,
				event.memo ?: context.getString(R.string.calendar_event_description)
			)
			.putExtra(
				CalendarContract.Events.CALENDAR_COLOR,
				getColorByClassType(event.classtypeId)
			)
			.putExtra(
				CalendarContract.Events.ACCESS_LEVEL,
				CalendarContract.Events.ACCESS_DEFAULT
			)
			.putExtra(
				CalendarContract.Events.AVAILABILITY,
				CalendarContract.Events.AVAILABILITY_BUSY
			)
		context.startActivity(
			Intent.createChooser(
				insertCalendarIntent,
				context.getString(R.string.create_event_title)
			)
		)
	}

	fun phoneIntent(context: Context, phoneNumber: String) {
		context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
	}

	@SuppressLint("QueryPermissionsNeeded")
	fun mapsIntent(
		context: Context,
		lat: Float?,
		long: Float?,
		name: String?
	) {
		val gmmIntentUri = Uri.parse("geo:$lat,$long?q=${Uri.encode(name)}")
		val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
		mapIntent.setPackage("com.google.android.apps.maps")
		mapIntent.resolveActivity(context.packageManager ?: return)
			?.let {
				context.startActivity(mapIntent)
			}
	}

	@Suppress("DEPRECATION")
	fun parseHtml(input: String): Spanned =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Html.fromHtml(input, Html.FROM_HTML_MODE_COMPACT)
		} else {
			Html.fromHtml(input)
		}

	@Suppress("DEPRECATION")
	fun stripHtmlTags(html: String) =
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT).toString()
				.replace(Regex(" +|\t+|\n+"), " ").replace(Regex(" {2,}"), "")
		} else {
			Html.fromHtml(html).toString().replace(Regex(" +|\t+|\n+"), " ")
				.replace(Regex(" {2,}"), "")
		}

	fun Context.themeColor(@AttrRes themeAttrId: Int) = obtainStyledAttributes(
		intArrayOf(themeAttrId)
	).use {
		it.getColor(0, Color.MAGENTA)
	}

	fun askForRelogin(
		activity: Activity,
		description: Int,
		onDismissListener: (() -> Unit)? = null
	) {
		DialogUtil.create(
			object : DialogUtil.StatefulDialog<DialogLoadingBinding>() {
				override fun toggle() {
					binding.progressBar.isVisible = true
					binding.messageMain.setText(R.string.loading)
					binding.messageSecondary.isGone = true
					binding.buttonPrimary.isGone = true
					binding.buttonSecondary.isGone = true
				}
			},
			activity, DialogLoadingBinding::class.java, { dialog ->
				dialog.messageMain.setText(R.string.there_been_a_problem)
				dialog.messageSecondary.setText(description)
				dialog.buttonPrimary.setText(R.string.logout)
				dialog.buttonPrimary.setOnClickListener {
					toggle()
					doAsync {
						Preferences.get().clear()
						AppDatabase.init(activity).clearAllTables()
						this@create.dialog.dismiss()
						activity.apply {
							startActivity(
								Intent(
									activity,
									ActivityLogin::class.java
								)
							)
							finish()
						}
					}
				}
				if (onDismissListener == null) {
					dialog.buttonSecondary.isGone = true
					this.dialog.setCancelable(false)
					this.dialog.setCanceledOnTouchOutside(false)
				}

				this.dialog.setOnDismissListener { onDismissListener?.invoke() }
				this.dialog.setOnCancelListener { onDismissListener?.invoke() }

				dialog.buttonSecondary.setOnClickListener {
					this.dialog.dismiss()
					onDismissListener?.invoke()
				}
			}
		)
	}

	fun showCourseInfo(
		context: Context,
		event: CourseEvent?,
		viewModelUser: ViewModelUser,
		onChangeFragment: ((FragmentCore<*>) -> Unit)?,
		onCommentChanged: ((String?) -> Unit)? = null,
		onRemoved: (() -> Unit)? = null
	) {
		if (event == null) {
			return
		}

		DialogUtil.create(context, DialogShowEventBinding::class.java, { dialog ->
			val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
			dialog.eventName.text = event.name()
			dialog.eventStartTime.text = hourFormat.format(event.startTime.time)
			dialog.eventEndTime.text = hourFormat.format(event.endTime?.time ?: return@create)
			dialog.eventType.text =
				event.classtypeName?.toString() ?: context.getString(R.string.meeting)
			dialog.eventMemo.text = event.memo?.takeUnless(String::isNullOrEmpty)?.also {
				dialog.eventMemo.isVisible = true
				dialog.buttonAddNote.setText(R.string.edit_the_note)
				dialog.buttonAddNote.setIconResource(R.drawable.ic_edit)
			}.toString()
			event.roomNumber?.takeIf(String::isNotBlank)?.also {
				dialog.buttonRoom.text = it
				dialog.buttonRoom.isVisible = true
			}
			event.buildingName?.takeUnless(Name::isEmpty)?.also {
				dialog.buttonBuilding.text = it.toString()
				dialog.buttonBuilding.isVisible = true
			}
			event.groupNumber?.toIntOrNull()?.also {
				dialog.buttonGroup.isVisible = true
				dialog.buttonGroup.text = context.resources.getString(R.string.group_number, it)
			}
			val userId = run {
				event.lecturerIds?.firstOrNull() ?: event.relatedUserIds?.firstOrNull()
			}

			when {
				userId != null -> viewModelUser.getUserById(userId)
					.onOnceCallback { (user) ->
						GlobalScope.launch(Dispatchers.Main) {
							user?.name()?.takeIf(String::isNotBlank)
								?.also { dialog.buttonLecturer.text = it }
						}
					}
				event.lecturerName != null -> dialog.buttonLecturer.text = event.lecturerName
				else -> dialog.buttonLecturer.isGone = true
			}

			dialog.buttonGroup.setOnClickListener {
				onChangeFragment?.invoke(FragmentCourse("${event.unitId}#${event.groupNumber}"))

				dismiss()
			}

			dialog.buttonLecturer.setOnClickListener {
				onChangeFragment?.invoke(FragmentUser(userId ?: return@setOnClickListener))

				dismiss()
			}

			dialog.buttonRoom.setOnClickListener {
				onChangeFragment?.invoke(FragmentRoom(event.roomId ?: return@setOnClickListener))

				dismiss()
			}

			dialog.buttonBuilding.setOnClickListener {
				onChangeFragment?.invoke(
					FragmentBuilding(
						event.buildingId ?: return@setOnClickListener
					)
				)

				dismiss()
			}

			dialog.buttonAddToCalendar.setOnClickListener { calendarIntent(context, event) }
			dialog.buttonAddNote.setOnClickListener {
				showEventMemoEdit(context, event) {
					dialog.eventMemo.isGone = it.isNullOrBlank()
					dialog.eventMemo.text = it?.takeUnless(String::isNullOrEmpty)?.also {
						dialog.buttonAddNote.setText(R.string.edit_the_note)
						dialog.buttonAddNote.setIconResource(R.drawable.ic_edit)
					}.toString()
					onCommentChanged?.invoke(it)
				}
			}

			if (onRemoved == null || (try {
					UUID.fromString(event.unitId)
				} catch (e: Exception) {
					null
				}) == null
			) {
				dialog.buttonRemove.isGone = true
			}
			dialog.buttonRemove.setOnClickListener {
				showMessage(
					R.string.are_you_sure,
					R.string.delete_event_description,
					R.string.remove,
					context = context
				) {
					RepositoryTimetable(context).deleteByCompositeId(
						event.courseId,
						event.unitId,
						event.startTime.time
					)
					dismiss()
					onRemoved?.invoke()
				}
			}
		})
	}

	fun showEventAdd(
		context: Context,
		startDate: Calendar,
		onSaved: (CourseEvent) -> Unit
	) {
		DialogUtil.create(context, DialogAddEventBinding::class.java, { dialog ->
			val startTime = startDate.clone() as Calendar
			val endTime = (startDate.clone() as Calendar).apply { add(Calendar.MINUTE, 45) }
			var color = ContextCompat.getColor(context, R.color.defaultEventColor)
			var endTimeRepeating: Calendar? = null
			var frequency = 0
			var lecturer: User? = null

			dialog.buttonEventColor.iconTint = ColorStateList.valueOf(color)
			dialog.buttonEventColor.setOnClickListener {
				ActivityWidgetSettings.showDialogColorPicker(context, R.string.event_color) {
					color = it
					dialog.buttonEventColor.iconTint = ColorStateList.valueOf(color)
				}
			}

			dialog.buttonEventDate.text =
				SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startTime.time)
			dialog.buttonEventStartTime.text =
				SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime.time)
			dialog.buttonEventEndTime.text =
				SimpleDateFormat("HH:mm", Locale.getDefault()).format(endTime.time)

			dialog.buttonEventDate.setOnClickListener {
				showDatePicker(context, startTime) {
					startTime.timeInMillis = it.timeInMillis
					dialog.buttonEventDate.text =
						SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(startTime.time)
				}
			}
			dialog.buttonEventStartTime.setOnClickListener {
				showTimePicker(context, startTime) {
					startTime.timeInMillis = it.timeInMillis
					dialog.buttonEventStartTime.text =
						SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime.time)
				}
			}
			dialog.buttonEventEndTime.setOnClickListener {
				showTimePicker(context, endTime) {
					endTime.timeInMillis = it.timeInMillis
					dialog.buttonEventEndTime.text =
						SimpleDateFormat("HH:mm", Locale.getDefault()).format(startTime.time)
				}
			}

			dialog.buttonEnableRepeating.setOnClickListener {
				showRepeating(context, frequency, endTimeRepeating, startTime) { f, eTR ->
					frequency = f
					endTimeRepeating = eTR
					if (frequency > 0 && endTimeRepeating != null) {
						dialog.buttonEnableRepeating.text = context.resources.getQuantityString(
							R.plurals.repeat_every_until,
							frequency,
							frequency,
							endTimeRepeating!!.time
						)
					} else {
						dialog.buttonEnableRepeating.setText(R.string.enable_repeating)
					}
				}
			}

			dialog.inputEventLecturer.apply {
				setAdapter(AdapterAutocomplete(context).apply {
					onItemClickListener =
						AdapterView.OnItemClickListener { _, _, position, _ ->
							list[position].also {
								lecturer = it.user
								dialog.inputEventLecturer.setText(it.name())
							}
						}

					isLongClickable = false
				})

				val repositoryUser = RepositoryUser(context)
				addTextChangedListener {
					if (isPerformingCompletion) {
						return@addTextChangedListener
					}

					(adapter as? AdapterAutocomplete)?.update(
						listOf(
							Email.Recipient(
								it.toString(),
								null
							)
						)
					)
					repositoryUser.getByQuery(it.toString()).onOnceCallback { (list) ->
						GlobalScope.launch(Dispatchers.Main) {
							(adapter as? AdapterAutocomplete)?.update(
								listOf(
									Email.Recipient(it.toString(), null),
									*list.map { user -> Email.Recipient(null, user) }.toTypedArray()
								)
							)
						}
					}
				}
			}

			setCanceledOnTouchOutside(false)
			setCancelable(false)

			dialog.buttonCancel.setOnClickListener {
				showMessage(
					R.string.discard_title,
					R.string.discard_message,
					R.string.discard,
					context = context
				) { dismiss() }
			}

			dialog.buttonSave.setOnClickListener {
				if (dialog.inputEventName.text.isNullOrBlank() ||
					dialog.inputEventLecturer.text.isNullOrBlank() ||
					dialog.inputEventLocation.text.isNullOrBlank()
				) {
					showMessage(
						R.string.there_been_a_problem,
						R.string.you_cannot_leave_empty_fields,
						android.R.string.ok,
						null,
						context
					)

					return@setOnClickListener
				}

				val event = CourseEvent(
					unitId = UUID.randomUUID().toString(),
					startTime = startTime.time,
					endTime = endTime.time,
					color = color,
					name = Name(pl = dialog.inputEventName.text.toString()),
					lecturerName = dialog.inputEventLecturer.text.toString(),
					lecturerIds = lecturer?.let { listOf(it.id) },
					roomNumber = dialog.inputEventLocation.text.toString(),
					endTimeRepeating = endTimeRepeating?.time,
					frequency = TimeUnit.DAYS.toMillis(frequency.toLong())
				)
				RepositoryTimetable(context).insert(event)
				dismiss()
				onSaved(event)
			}
		})
	}

	private fun showRepeating(
		context: Context,
		startFrequency: Int,
		startEndTimeRepeating: Calendar?,
		minTime: Calendar,
		onSaved: (Int, Calendar?) -> Unit
	) {
		var frequency = startFrequency.coerceAtLeast(1)
		val endTimeRepeating = (startEndTimeRepeating ?: minTime).clone() as Calendar
		DialogUtil.create(context, DialogRepeatingBinding::class.java, { dialog ->
			dialog.buttonFrequency.text = frequency.toString()
			dialog.textDays.text =
				context.resources.getQuantityString(R.plurals.day, frequency)

			dialog.buttonEndTimeRepeating.text =
				SimpleDateFormat(
					"dd.MM.yyyy",
					Locale.getDefault()
				).format(endTimeRepeating.time)

			dialog.buttonFrequency.setOnClickListener {
				ActivityWidgetSettings.showDialogSlider(
					context,
					R.string.repeat_every,
					1,
					14,
					frequency.coerceAtLeast(1)
				) {
					frequency = it.toInt()
					dialog.buttonFrequency.text = it
					dialog.textDays.text =
						context.resources.getQuantityString(R.plurals.day, frequency)
				}
			}
			dialog.buttonEndTimeRepeating.setOnClickListener {
				showDatePicker(context, endTimeRepeating) {
					endTimeRepeating.timeInMillis = it.timeInMillis
					dialog.buttonEndTimeRepeating.text =
						SimpleDateFormat(
							"dd.MM.yyyy",
							Locale.getDefault()
						).format(endTimeRepeating.time)
				}
			}
			dialog.buttonDisable.setOnClickListener {
				dismiss()
				onSaved(0, null)
			}
			dialog.buttonSave.setOnClickListener {
				dismiss()
				onSaved(frequency, endTimeRepeating)
			}
		})
	}

	private fun showDatePicker(
		context: Context,
		startDate: Calendar,
		onPicked: (Calendar) -> Unit
	) {
		DialogUtil.create(context, DialogDatePickerBinding::class.java, { dialog ->
			val date = startDate.clone() as Calendar
			dialog.datePicker.minDate = startDate.timeInMillis
			dialog.datePicker.init(
				startDate.get(Calendar.YEAR),
				startDate.get(Calendar.MONTH),
				startDate.get(Calendar.DAY_OF_MONTH)
			) { _, year, month, day ->
				date.set(Calendar.YEAR, year)
				date.set(Calendar.MONTH, month)
				date.set(Calendar.DAY_OF_MONTH, day)
			}

			dialog.buttonPrimary.setOnClickListener {
				onPicked(date)
				dismiss()
			}
			dialog.buttonSecondary.setOnClickListener { dismiss() }
		})
	}

	@Suppress("DEPRECATION")
	private fun showTimePicker(
		context: Context,
		startTime: Calendar,
		onPicked: (Calendar) -> Unit
	) {
		DialogUtil.create(context, DialogTimePickerBinding::class.java, { dialog ->
			val date = startTime.clone() as Calendar
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				dialog.timePicker.hour = startTime.get(Calendar.HOUR_OF_DAY)
				dialog.timePicker.minute = startTime.get(Calendar.MINUTE)
			} else {
				dialog.timePicker.currentHour = startTime.get(Calendar.HOUR_OF_DAY)
				dialog.timePicker.currentMinute = startTime.get(Calendar.MINUTE)
			}
			dialog.timePicker.setIs24HourView(true)
			dialog.timePicker.setOnTimeChangedListener { _, hour, minute ->
				date.set(Calendar.HOUR_OF_DAY, hour)
				date.set(Calendar.MINUTE, minute)
			}

			dialog.buttonPrimary.setOnClickListener {
				onPicked(date)
				dismiss()
			}
			dialog.buttonSecondary.setOnClickListener { dismiss() }
		})
	}

	private fun showMessage(
		title: Int,
		description: Int,
		buttonPrimary: Int,
		buttonSecondary: Int? = R.string.cancel,
		context: Context,
		onPrimaryClicked: (() -> Unit)? = null
	) {
		DialogUtil.create(
			context,
			DialogMessageBinding::class.java,
			{ dialog ->
				dialog.messageMain.text = context.getString(title)
				dialog.messageSecondary.text = context.getString(description)
				dialog.buttonPrimary.text = context.getString(buttonPrimary)
				if (buttonSecondary != null) {
					dialog.buttonSecondary.text = context.getString(buttonSecondary)
				} else {
					dialog.buttonSecondary.isGone = true
				}

				dialog.buttonPrimary.setOnClickListener {
					dismiss()
					onPrimaryClicked?.invoke()
				}
				dialog.buttonSecondary.setOnClickListener { dismiss() }
			})
	}

	private fun showEventMemoEdit(
		context: Context,
		event: CourseEvent,
		onCommentChanged: ((String?) -> Unit)?
	) {
		DialogUtil.create(context, DialogMemoBinding::class.java, { dialog ->
			dialog.memoContent.setText(event.memo ?: "")

			dialog.buttonPrimary.setOnClickListener {
				event.memo = dialog.memoContent.text?.toString()
				RepositoryTimetable(context).insert(event)
				dismiss()
				onCommentChanged?.invoke(event.memo)
			}
			dialog.buttonSecondary.setOnClickListener { dismiss() }
		})
	}

	open class SingletonHolder<T : Any, in A>(creator: (A?) -> T) {
		private var creator: ((A?) -> T)? = creator

		@Volatile
		protected var instance: T? = null

		fun init(arg: A? = null): T {
			return synchronized(this) {
				if (instance != null) instance!!
				else {
					val created = creator!!(arg)
					instance = created
					creator = null
					created
				}
			}
		}
	}
}
