package com.pointlessapps.mobileusos.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.text.Html
import android.text.Spanned
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.activities.ActivityLogin
import com.pointlessapps.mobileusos.databinding.DialogLoadingBinding
import com.pointlessapps.mobileusos.databinding.DialogMemoBinding
import com.pointlessapps.mobileusos.databinding.DialogShowEventBinding
import com.pointlessapps.mobileusos.fragments.*
import com.pointlessapps.mobileusos.helpers.Preferences
import com.pointlessapps.mobileusos.models.AppDatabase
import com.pointlessapps.mobileusos.models.CourseEvent
import com.pointlessapps.mobileusos.models.Name
import com.pointlessapps.mobileusos.repositories.RepositoryTimetable
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*


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

	fun getColorByClassType(classType: String) = when (classType.toLowerCase(Locale.getDefault())) {
		"wyk" -> Color.parseColor("#FF80CBC4")
		"ćw" -> Color.parseColor("#FFF06292")
		"lab" -> Color.parseColor("#FFFF9E80")
		"wf" -> Color.parseColor("#FF81C784")
		else -> Color.parseColor("#FFAED581")
	}

	fun calendarIntent(context: Context, event: CourseEvent) {
		val insertCalendarIntent = Intent(Intent.ACTION_INSERT)
			.setData(CalendarContract.Events.CONTENT_URI)
			.putExtra(CalendarContract.Events.TITLE, event.courseName.toString())
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
				"${event.buildingName.toString()}, ${event.roomNumber}"
			)
			.putExtra(
				CalendarContract.Events.DESCRIPTION,
				context.getString(R.string.calendar_event_description)
			)
			.putExtra(
				CalendarContract.Events.CALENDAR_COLOR,
				getColorByClassType(event.classtypeId ?: "")
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
				}

				dialog.buttonSecondary.setOnClickListener {
					this.dialog.dismiss()
					onDismissListener?.invoke()
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT
		)
	}

	fun showCourseInfo(
		context: Context,
		event: CourseEvent?,
		viewModelUser: ViewModelUser,
		onChangeFragment: ((FragmentCore<*>) -> Unit)?
	) {
		if (event == null) {
			return
		}

		DialogUtil.create(context, DialogShowEventBinding::class.java, { dialog ->
			val hourFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
			dialog.eventName.text = event.courseName.toString()
			dialog.eventStartTime.text = hourFormat.format(event.startTime.time)
			dialog.eventEndTime.text = hourFormat.format(event.endTime?.time ?: return@create)
			dialog.eventType.text = event.classtypeName.toString()
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
			viewModelUser.getUserById(event.lecturerIds?.firstOrNull() ?: return@create)
				.onOnceCallback { (user) ->
					GlobalScope.launch(Dispatchers.Main) {
						user?.name()?.takeIf(String::isNotBlank)
							?.also { dialog.buttonLecturer.text = it }
					}
				}

			dialog.buttonGroup.setOnClickListener {
				onChangeFragment?.invoke(FragmentCourse("${event.unitId}#${event.groupNumber}"))

				dismiss()
			}

			dialog.buttonLecturer.setOnClickListener {
				onChangeFragment?.invoke(
					FragmentUser(
						event.lecturerIds?.firstOrNull() ?: return@setOnClickListener
					)
				)

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
				showEventMemoEdit(context, event)
				dismiss()
			}
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ConstraintLayout.LayoutParams.WRAP_CONTENT)
	}

	private fun showEventMemoEdit(context: Context, event: CourseEvent) {
		DialogUtil.create(context, DialogMemoBinding::class.java, { dialog ->
			dialog.memoContent.setText(event.memo ?: "")

			dialog.buttonPrimary.setOnClickListener {
				event.memo = dialog.memoContent.text.toString()
				RepositoryTimetable(context).insert(event)
				dismiss()
			}
			dialog.buttonSecondary.setOnClickListener { dismiss() }
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}

	open class SingletonHolder<T : Any, in A>(creator: (A?) -> T) {
		private var creator: ((A?) -> T)? = creator

		@Volatile
		protected var instance: T? = null

		fun init(arg: A? = null): T {
			if (instance != null) return instance!!

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
