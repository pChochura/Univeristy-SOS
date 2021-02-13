package com.pointlessapps.mobileusos.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.intrusoft.sectionedrecyclerview.Section
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.databinding.ListItemTestPartChildBinding
import com.pointlessapps.mobileusos.databinding.ListItemTestPartHeaderBinding
import com.pointlessapps.mobileusos.models.Test

class AdapterTestPart(private val context: Context, sections: List<SectionHeader> = listOf()) :
	SectionRecyclerViewAdapter<AdapterTestPart.SectionHeader, Test.Node, AdapterTestPart.SectionViewHolder, AdapterTestPart.ChildViewHolder>(
		context,
		sections
	) {

	lateinit var onClickListener: (Test.Node) -> Unit

	override fun onCreateSectionViewHolder(itemView: ViewGroup, viewType: Int) =
		SectionViewHolder(
			ListItemTestPartHeaderBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindSectionViewHolder(
		itemView: SectionViewHolder,
		sectionPosition: Int,
		section: SectionHeader
	) {
		itemView.binding.nodeName.setText(R.string.other)

		section.getSectionHeader()?.also { node ->
			itemView.binding.containerPoints.isVisible = true
			itemView.binding.nodeName.text = node.name.toString()

			node.studentsPoints?.apply {
				node.folderNodeDetails?.pointsMax?.also {
					itemView.binding.nodePoints.text =
						"%d / %d".format(Integer.max(automaticPoints, points), it)

					return@apply
				}

				itemView.binding.nodePoints.text = Integer.max(automaticPoints, points).toString()
			}
		}
	}

	override fun onCreateChildViewHolder(itemView: ViewGroup, viewType: Int) =
		ChildViewHolder(
			ListItemTestPartChildBinding.inflate(
				LayoutInflater.from(context),
				itemView,
				false
			)
		)

	override fun onBindChildViewHolder(
		itemView: ChildViewHolder,
		sectionPosition: Int,
		childPosition: Int,
		node: Test.Node
	) {
		itemView.binding.root.setOnClickListener {
			onClickListener.invoke(node)
		}

		itemView.binding.nodeName.text = node.name.toString()

		when (node.type) {
			Test.Node.GRADE -> node.gradeNodeDetails?.apply {
				studentsGrade?.gradeValue?.symbol?.also { grade ->
					itemView.binding.nodeGrade.text = grade

					return@apply
				}
				studentsGrade?.automaticGradeValue?.symbol?.also { grade ->
					itemView.binding.nodeGrade.text = grade

					return@apply
				}
				gradeType?.values?.firstOrNull()?.symbol?.also { grade ->
					itemView.binding.nodeGrade.text = grade

					return@apply
				}

				itemView.binding.nodeGrade.setText(R.string.empty)
			}
			Test.Node.TASK -> node.studentsPoints?.apply {
				node.taskNodeDetails?.pointsMax?.also {
					itemView.binding.nodeGrade.text =
						"%d / %d".format(Integer.max(automaticPoints, points), it)

					return@apply
				}

				itemView.binding.nodeGrade.text = Integer.max(automaticPoints, points).toString()
			}
		}

	}

	class SectionHeader(private val header: Test.Node?, private val children: List<Test.Node>) :
		Section<Test.Node> {
		override fun getChildItems() = children
		fun getSectionHeader() = header
	}

	class SectionViewHolder(val binding: ListItemTestPartHeaderBinding) :
		RecyclerView.ViewHolder(binding.root)

	class ChildViewHolder(val binding: ListItemTestPartChildBinding) :
		RecyclerView.ViewHolder(binding.root)
}
