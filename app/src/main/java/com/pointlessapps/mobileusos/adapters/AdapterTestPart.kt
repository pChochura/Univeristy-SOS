package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Test
import org.jetbrains.anko.find

class AdapterTestPart(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterTestPart.SectionHeader, Test.Node, AdapterTestPart.SectionViewHolder, AdapterTestPart.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Test.Node) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			LayoutInflater.from(context)
				.inflate(R.layout.list_item_test_part_header, itemView, false)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.textName.setText(R.string.other)

		section.getSectionHeader()?.also { node ->
			itemView.containerPoints.isVisible = true
			itemView.textName.text = node.name.toString()

			node.studentsPoints?.apply {
				node.folderNodeDetails?.pointsMax?.also {
					itemView.textPoints.text =
						"%d / %d".format(Integer.max(automaticPoints, points), it)

					return@apply
				}

				itemView.textPoints.text = Integer.max(automaticPoints, points).toString()
			}
		}
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			LayoutInflater.from(context)
				.inflate(R.layout.list_item_test_part_child, itemView, false)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		node: Test.Node
	) {
		itemView.bg.setOnClickListener {
			onClickListener.invoke(node)
		}

		itemView.textName.text = node.name.toString()

		when (node.type) {
			Test.Node.GRADE -> node.gradeNodeDetails?.apply {
				studentsGrade?.gradeValue?.symbol?.also { grade ->
					itemView.textGrade.text = grade

					return@apply
				}
				studentsGrade?.automaticGradeValue?.symbol?.also { grade ->
					itemView.textGrade.text = grade

					return@apply
				}
				gradeType?.values?.firstOrNull()?.symbol?.also { grade ->
					itemView.textGrade.text = grade

					return@apply
				}

				itemView.textGrade.setText(R.string.empty)
			}
			Test.Node.TASK -> node.studentsPoints?.apply {
				node.taskNodeDetails?.pointsMax?.also {
					itemView.textGrade.text =
						"%d / %d".format(Integer.max(automaticPoints, points), it)

					return@apply
				}

				itemView.textGrade.text = Integer.max(automaticPoints, points).toString()
			}
		}

	}

	class SectionHeader(private val header: Test.Node?, private val children: List<Test.Node>) :
		Section<Test.Node> {
		override fun getChildItems() = children
		fun getSectionHeader() = header
	}

	class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val textName = itemView.find<AppCompatTextView>(R.id.nodeName)
		val textPoints = itemView.find<AppCompatTextView>(R.id.nodePoints)
		val containerPoints = itemView.find<View>(R.id.containerPoints)
	}

	class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val bg = itemView.find<View>(R.id.bg)
		val textName = itemView.find<AppCompatTextView>(R.id.nodeName)
		val textGrade = itemView.find<Chip>(R.id.nodeGrade)
	}
}
