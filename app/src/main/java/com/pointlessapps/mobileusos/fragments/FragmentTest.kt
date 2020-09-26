package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterTestPart
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.RoundedBarChartRenderer
import com.pointlessapps.mobileusos.utils.Utils.themeColor
import com.pointlessapps.mobileusos.utils.dp
import com.pointlessapps.mobileusos.utils.fromJson
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.dialog_show_grade.*
import kotlinx.android.synthetic.main.fragment_test.view.*
import org.jetbrains.anko.doAsync
import java.lang.Integer.max
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class FragmentTest(private val json: String) : FragmentBase(), FragmentPinnable {

	constructor(test: Test) : this(Gson().toJson(test))

	private val test = Gson().fromJson<Test>(json)
	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_test

	override fun getShortcut(fragment: FragmentBase, callback: (Pair<Int, String>) -> Unit) {
		callback(R.drawable.ic_test to test.courseEdition.course?.name.toString())
	}

	override fun created() {
		prepareClickListeners()
		prepareTestPartsList()
		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		root().testName.text = test.courseEdition.course?.name.toString()
		root().testDescription.text = test.root?.description.toString()
		if (test.root?.description?.isEmpty() == true) {
			root().testDescription.isGone = true
			root().buttonDescription.isGone = true
		}
		if (!test.isLimitedToGroups || test.classGroups == null) {
			root().testParticipants.setText(R.string.all_participants)
		} else {
			root().testParticipants.text =
				getString(
					R.string.groups_other,
					test.classGroups?.joinToString { it.number.toString() })
		}

		val loaded = CountDownLatch(1)
		viewModelUser.getTestNodesByIds(test.root?.subNodes?.map(Test.Node::id) ?: listOf())
			.observe(this) { (nodes, sourceType) ->
				root().horizontalProgressBar.isRefreshing = true

				val partition = nodes.partition { it.type != Test.Node.GRADE }
				(root().listTestParts.adapter as? AdapterTestPart)?.notifyDataChanged(
					listOf(
						*partition.first.map {
							AdapterTestPart.SectionHeader(
								it,
								it.subNodes?.sorted() ?: listOf()
							)
						}.toTypedArray(),
						AdapterTestPart.SectionHeader(null, partition.second.sorted())
					).sortedBy { it.getSectionHeader()?.order ?: 100 }
				)

				prepareSubNodes(nodes) { loaded.countDown() }
			}

		doAsync {
			loaded.await()
			root().pullRefresh.isRefreshing = false
			root().horizontalProgressBar.isRefreshing = false
		}

		if (isPinned(javaClass.name, json)) {
			root().buttonPin.setIconResource(R.drawable.ic_unpin)
		}
	}

	private fun prepareSubNodes(nodes: List<Test.Node>, callback: () -> Unit) {
		val folderNodes = nodes.filter { it.type == Test.Node.FOLDER }
		viewModelUser.getTestNodesByIds(folderNodes.flatMap {
			it.subNodes?.map(Test.Node::id) ?: listOf()
		}).observe(this) { (subNodes) ->
			subNodes.forEach { subNode ->
				folderNodes.flatMap { it.subNodes ?: listOf() }.find { it.id == subNode.id }
					?.set(subNode)
			}
		}.onFinished {
			val partition = nodes.partition { it.type != Test.Node.GRADE }
			(root().listTestParts.adapter as? AdapterTestPart)?.notifyDataChanged(
				listOf(
					*partition.first.map {
						AdapterTestPart.SectionHeader(
							it,
							it.subNodes?.sorted() ?: listOf()
						)
					}.toTypedArray(),
					AdapterTestPart.SectionHeader(null, partition.second.sorted())
				).sortedBy { it.getSectionHeader()?.order ?: 100 }
			)

			callback()
		}
	}

	private fun prepareClickListeners() {
		setCollapsible(root().buttonDescription, root().testDescription)

		root().buttonPin.setOnClickListener {
			root().buttonPin.setIconResource(
				if (togglePin(javaClass.name, json))
					R.drawable.ic_unpin
				else R.drawable.ic_pin
			)

			onForceRefreshAllFragments?.invoke()
		}
	}

	private fun prepareTestPartsList() {
		val partition = test.root?.subNodes?.partition { it.type != Test.Node.GRADE } ?: Pair(
			listOf(),
			listOf()
		)
		root().listTestParts.setAdapter(AdapterTestPart(
			requireContext(), listOf(
				*partition.first.map {
					AdapterTestPart.SectionHeader(
						it,
						it.subNodes?.sorted() ?: listOf()
					)
				}.toTypedArray(),
				AdapterTestPart.SectionHeader(null, partition.second.sorted())
			).sortedBy { it.getSectionHeader()?.order ?: 100 }
		).apply {
			onClickListener = {
				if (it.type != Test.Node.FOLDER) {
					showGradeDialog(it)
				}
			}
		})
	}

	private fun showGradeDialog(node: Test.Node) {
		DialogUtil.create(requireContext(), R.layout.dialog_show_grade, { dialog ->
			dialog.gradeName.text = node.name.toString()

			var grade = getString(R.string.empty)
			when (node.type) {
				Test.Node.GRADE -> node.gradeNodeDetails?.apply {
					studentsGrade?.gradeValue?.symbol?.also { grade = it }
					studentsGrade?.automaticGradeValue?.symbol?.also { grade = it }
					gradeType?.values?.firstOrNull()?.symbol?.also { grade = it }
				}
				Test.Node.TASK -> node.studentsPoints?.apply {
					grade = max(automaticPoints, points).toString()
					node.taskNodeDetails?.pointsMax?.also {
						grade = "%d / %d".format(max(automaticPoints, points), it)
					}
				}
			}

			dialog.gradeValue.text = grade

			if (node.description?.isEmpty() == false) {
				dialog.gradeDescription.isVisible = true
				dialog.gradeDescription.text = node.description.toString()
			}

			if (node.gradeNodeDetails?.studentsGrade?.comment?.isEmpty() == false) {
				dialog.gradeDescription.isVisible = true
				dialog.gradeDescription.text =
					node.gradeNodeDetails?.studentsGrade?.comment.toString()
			}

			dialog.buttonGradeAuthor.apply {
				text = node.gradeNodeDetails?.studentsGrade?.grader?.name()
					?: node.studentsPoints?.grader?.name()
							?: getString(R.string.empty)
				setOnClickListener {
					dialog.dismiss()
					onChangeFragment?.invoke(
						FragmentUser(
							node.gradeNodeDetails?.studentsGrade?.grader?.id
								?: return@setOnClickListener
						)
					)
				}
			}

			dialog.buttonGradeDate.text = SimpleDateFormat(
				"yyyy-MM-dd H:mm",
				Locale.getDefault()
			).format(
				node.gradeNodeDetails?.studentsGrade?.lastChanged
					?: node.studentsPoints?.lastChanged ?: Date()
			)

			val minGrade = if (node.type == Test.Node.GRADE) 4f else 0f
			val values = node.stats?.map { stats ->
				BarEntry(
					(stats["value"]?.replace(Regex("(\\d)[.,](\\d).*")) {
						"${it.groups[1]?.value}.${it.groups[2]?.value}"
					}?.toFloatOrNull() ?: 2f) * 2 - minGrade,
					stats["number_of_values"]?.toFloatOrNull() ?: 0f
				)
			} ?: listOf()
			when (node.type) {
				Test.Node.GRADE -> FragmentGrades.prepareChart(requireContext(), dialog, "%.0f")
				Test.Node.TASK -> FragmentGrades.prepareChart(
					requireContext(),
					dialog,
					"%.0f",
					listOf(
						*(0..(max(
							node.studentsPoints?.automaticPoints ?: 0,
							node.studentsPoints?.points ?: 0
						) * 2)).toList().map { (it / 2).toString() }.toTypedArray()
					)
				)
			}
			dialog.gradeChart.apply {
				this.data = BarData(BarDataSet(values, "").apply {
					valueTypeface =
						ResourcesCompat.getFont(context, R.font.montserrat)
					valueTextSize = 10f
					valueFormatter = object : ValueFormatter() {
						override fun getBarLabel(barEntry: BarEntry) =
							"%.0f".format(barEntry.y)
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
						highlightedIndex = grade.run {
							((replace(Regex("(\\d)[.,](\\d).*")) {
								"${it.groups[1]?.value}.${it.groups[2]?.value}"
							}.toFloatOrNull() ?: 2f) * 2 - minGrade).toInt() - 1
						}
					}
				when (node.type) {
					Test.Node.GRADE -> setVisibleXRange(2f, 7f)
					Test.Node.TASK -> setVisibleXRange(
						0f,
						(node.taskNodeDetails?.pointsMax ?: max(
							node.studentsPoints?.automaticPoints ?: 1,
							node.studentsPoints?.points ?: 1
						)).toFloat()
					)
				}
				invalidate()
			}
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}

	private fun setCollapsible(button: MaterialButton, view: AppCompatTextView) {
		button.setOnClickListener {
			view.isVisible.also {
				view.isVisible = !it
				button.setIconResource(
					if (it) {
						R.drawable.ic_arrow_down
					} else {
						R.drawable.ic_arrow_up
					}
				)
				TransitionManager.beginDelayedTransition(root(), AutoTransition())
			}
		}
	}
}
