package com.pointlessapps.mobileusos.fragments

import android.app.Dialog
import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterGrade
import com.pointlessapps.mobileusos.models.Grade
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.RoundedBarChartRenderer
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_show_grade.*
import kotlinx.android.synthetic.main.fragment_grades.view.*
import java.text.SimpleDateFormat
import java.util.*

class FragmentGrades : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_grades

	override fun created() {
		prepareGradesList()
		prepareGrades()
	}

	private fun prepareGrades() {
		viewModelUser.getAllGroups().observe(this) { groups ->
			val termIds = groups?.map { group -> group.termId } ?: return@observe

			viewModelUser.getTermsByIds(termIds).observe(this) { terms ->
				viewModelUser.getGradesByTermIds(termIds).observe(this) { grades ->
					if (grades == null) {
						return@observe
					}

					(root().listGrades.adapter as? AdapterGrade)
						?.notifyDataChanged(
							terms?.map {
								AdapterGrade.SectionHeader(
									it,
									groups.distinctBy { group -> group.courseId }
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
							}?.sortedByDescending { it.getSectionHeader().orderKey }
						)
				}
			}
		}
	}

	private fun prepareGradesList() {
		root().listGrades.setAdapter(AdapterGrade(requireContext()).apply {
			onClickListener = { grade ->
				showGradeDialog(this@FragmentGrades, grade, viewModelUser)
			}
		})
	}

	companion object {
		fun showGradeDialog(fragment: Fragment, grade: Grade, viewModelUser: ViewModelUser) {
			DialogUtil.create(
				fragment.requireContext(),
				R.layout.dialog_show_grade,
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
					grade.modificationAuthor?.also { dialog.buttonGradeAuthor.text = it.name() }

					viewModelUser.getExamReportById(grade.examId ?: return@create)
						.observe(fragment) { examReport ->
							val values =
								examReport.gradesDistribution?.map { distribution ->
									BarEntry(
										distribution.gradeSymbol.replace(Regex("(\\d),(\\d).*")) {
											"${it.groups[1]?.value}.${it.groups[2]?.value}"
										}.toFloat() * 2 - 4,
										distribution.percentage.toFloat()
									)
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
									color = ContextCompat.getColor(context, R.color.colorAccent)
									valueTextColor =
										ContextCompat.getColor(context, R.color.colorTextPrimary)
								}).apply {
									barWidth = 1.5f
								}
								renderer =
									RoundedBarChartRenderer(
										this@apply,
										animator,
										viewPortHandler,
										2.dp.toFloat()
									)
								setVisibleXRange(2f, 7f)
								invalidate()
							}
						}
				},
				DialogUtil.UNDEFINED_WINDOW_SIZE,
				ConstraintLayout.LayoutParams.WRAP_CONTENT
			)
		}

		private fun prepareChart(context: Context, dialog: Dialog) {
			dialog.gradeChart.apply {
				legend.isEnabled = false
				description.isEnabled = false
				isHighlightPerDragEnabled = false
				isHighlightPerTapEnabled = false
				setNoDataTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
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
							listOf("2.0", "2.5", "3.0", "3.5", "4.0", "4.5", "5.0")[value.toInt()]
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
							"%.0f%%".format(value)
					}
				}
				axisLeft.apply {
					formatAxis(context)
					setDrawAxisLine(false)
					enableGridDashedLine(10f, 10f, 0f)
					axisMinimum = 0f
					valueFormatter = object : ValueFormatter() {
						override fun getAxisLabel(value: Float, axis: AxisBase?) =
							"%f%%".format(value)
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
			textColor = ContextCompat.getColor(context, R.color.colorTextPrimary)
		}
	}
}
