package com.pointlessapps.mobileusos.fragments

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterGrade
import com.pointlessapps.mobileusos.databinding.DialogShowGradeBinding
import com.pointlessapps.mobileusos.databinding.FragmentGradesBinding
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.RoundedBarChartRenderer
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import java.text.SimpleDateFormat
import java.util.*

class FragmentGrades : FragmentCoreImpl<FragmentGradesBinding>(FragmentGradesBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun created() {
		prepareGradesList()
		prepareGrades()

		binding().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		prepareGrades {
			binding().pullRefresh.isRefreshing = false
		}
	}

	private fun prepareGrades(callback: (() -> Unit)? = null) {
		var finished1 = false
		viewModelUser.getAllGroups().observe(this) { (groups) ->
			val termIds = groups.map { group -> group.termId }.distinct()
			var finished2 = false
			viewModelUser.getTermsByIds(termIds).observe(this) { (terms) ->
				viewModelUser.getGradesByTermIds(termIds).observe(this) { (grades) ->
					(binding().listGrades.adapter as? AdapterGrade)
						?.notifyDataChanged(
							terms.map {
								AdapterGrade.SectionHeader(
									it,
									groups.distinctBy { group -> group.courseId to group.termId }
										.filter { group -> group.termId == it.id }.map { group ->
											grades[it.id]?.get(group.courseId)?.also { grade ->
												grade.courseName = group.courseName
											} ?: Grade(
												countsIntoAverage = "N",
												courseName = group.courseName,
												courseId = group.courseId,
												termId = group.termId
											)
										}
								)
							}.sortedByDescending { it.getSectionHeader().orderKey }
						)

					binding().listGrades.apply {
						setEmptyText(getString(R.string.no_grades))
						setLoaded(false)
					}
				}.onFinished {
					if (finished1 && finished2) {
						binding().listGrades.setLoaded(true)
						callback?.invoke()
					}
				}
			}.onFinished { finished2 = true }
		}.onFinished { finished1 = true }
	}

	private fun prepareGradesList() {
		binding().listGrades.setAdapter(AdapterGrade(requireContext()).apply {
			onClickListener = { grade ->
				showGradeDialog(this@FragmentGrades, grade, viewModelUser)
			}
		})
	}

	companion object {
		fun showGradeDialog(
			fragment: FragmentCoreImpl<*>,
			grade: Grade,
			viewModelUser: ViewModelUser
		) {
			DialogUtil.create(
				fragment.requireContext(),
				DialogShowGradeBinding::class.java,
				{ dialog ->
					prepareChart(fragment.requireContext(), dialog)

					dialog.gradeName.text = grade.courseName?.toString()
					grade.valueSymbol?.also { dialog.gradeValue.text = it }
					grade.dateModified?.also {
						dialog.buttonGradeDate.text = SimpleDateFormat(
							"yyyy-MM-dd H:mm",
							Locale.getDefault()
						).format(it)
					}
					grade.modificationAuthor?.also {
						dialog.buttonGradeAuthor.text = it.name()
					}

					dialog.buttonGradeAuthor.setOnClickListener {
						fragment.onChangeFragment?.invoke(
							FragmentUser(
								grade.modificationAuthor?.id ?: return@setOnClickListener
							)
						)

						dismiss()
					}

					viewModelUser.getExamReportById(grade.examId ?: return@create)
						.observe(fragment) { (examReport) ->
							val values =
								examReport?.gradesDistribution?.map { distribution ->
									BarEntry(
										distribution.gradeSymbol.replace(Regex("(\\d),(\\d).*")) {
											"${it.groups[1]?.value}.${it.groups[2]?.value}"
										}.toFloat() * 2 - 4,
										distribution.percentage.toFloat()
									)
								} ?: listOf()

							if (values.isEmpty()) {
								return@observe
							}

							dialog.gradeChart.apply {
								this.data = BarData(BarDataSet(values, "").apply {
									valueTypeface =
										ResourcesCompat.getFont(context, R.font.montserrat)
									valueTextSize = 10f
									valueFormatter = object : ValueFormatter() {
										override fun getBarLabel(barEntry: BarEntry) =
											"%.0f%%".format(barEntry.y)
									}
									color =
										ContextCompat.getColor(context, R.color.colorTextPrimary)
									valueTextColor = context.themeColor(R.attr.colorTextPrimary)
								}).apply {
									barWidth = 1.5f
								}
								renderer =
									RoundedBarChartRenderer(
										this@apply,
										animator,
										viewPortHandler,
										2.dp.toFloat()
									).apply {
										highlightedColor =
											ContextCompat.getColor(context, R.color.colorAccent)
										highlightedIndex = grade.valueSymbol?.run {
											(replace(Regex("(\\d),(\\d).*")) {
												"${it.groups[1]?.value}.${it.groups[2]?.value}"
											}.toFloat() * 2 - 4).toInt() - 1
										}
									}
								setVisibleXRange(2f, 7f)
								invalidate()
							}
						}
				}
			)
		}

		fun prepareChart(
			context: Context,
			dialog: DialogShowGradeBinding,
			numberFormat: String = "%.0f%%",
			values: List<String> = listOf(
				"2.0",
				"2.5",
				"3.0",
				"3.5",
				"4.0",
				"4.5",
				"5.0"
			)
		) {
			dialog.gradeChart.apply {
				legend.isEnabled = false
				description.isEnabled = false
				isHighlightPerDragEnabled = false
				isHighlightPerTapEnabled = false
				setNoDataTextColor(context.themeColor(R.attr.colorTextPrimary))
				setNoDataTextTypeface(ResourcesCompat.getFont(context, R.font.montserrat))
				setScaleEnabled(false)
				xAxis.apply {
					formatAxis(context)
					setDrawGridLines(false)
					setDrawAxisLine(false)
					granularity = 1f
					position = XAxis.XAxisPosition.BOTTOM
					valueFormatter = object : IndexAxisValueFormatter() {
						override fun getFormattedValue(value: Float) =
							values[value.toInt()]
					}
				}
				axisRight.apply {
					formatAxis(context)
					setDrawAxisLine(false)
					granularity = 10f
					enableGridDashedLine(10f, 10f, 0f)
					axisMinimum = 0f
					valueFormatter = object : ValueFormatter() {
						override fun getAxisLabel(value: Float, axis: AxisBase?) =
							numberFormat.format(value)
					}
				}
				axisLeft.apply {
					formatAxis(context)
					setDrawAxisLine(false)
					enableGridDashedLine(10f, 10f, 0f)
					axisMinimum = 0f
					valueFormatter = object : ValueFormatter() {
						override fun getAxisLabel(value: Float, axis: AxisBase?) =
							numberFormat.format(value)
					}
					setDrawLabels(false)
				}
			}
		}

		private fun AxisBase.formatAxis(context: Context) {
			typeface = ResourcesCompat.getFont(context, R.font.montserrat)
			textSize = 8f
			granularity = 1f
			isGranularityEnabled = true
			setCenterAxisLabels(false)
			textColor = context.themeColor(R.attr.colorTextPrimary)
		}
	}
}
