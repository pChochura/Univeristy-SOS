package com.pointlessapps.mobileusos.fragments

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterTestPart
import com.pointlessapps.mobileusos.models.Test
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import kotlinx.android.synthetic.main.fragment_test.view.*

class FragmentTest(private val test: Test) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()

	override fun getLayoutId() = R.layout.fragment_test

	override fun created() {
		prepareClickListeners()
		prepareTestPartsList()
		refreshed()

		root().pullRefresh.setOnRefreshListener { refreshed() }
	}

	override fun refreshed() {
		Log.d(
			"LOG!",
			"${Gson().toJson(test.root?.taskNodeDetails)}, ${Gson().toJson(test.root?.gradeNodeDetails)}, ${
				Gson().toJson(
					test.root?.folderNodeDetails
				)
			}"
		)
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

		viewModelUser.getTestNodesByIds(test.root?.subNodes?.map { it.id } ?: listOf())
			.observe(this) { (nodes) ->
				nodes.forEach { node ->
					test.root?.subNodes?.find { it.id == node.id }?.resultObject =
						Test.Node.ResultObject(
							grade = node.gradeNodeDetails?.studentsGrade?.gradeValue
						)
				}
				Log.d("LOG!", "nodes: ${Gson().toJson(nodes)}")
			}.onFinished {
				Log.d("LOG!", "error: ${it.toString()}")
			}

//		viewModelUser.getTestResultsByNodeIds(test.root?.subNodes?.map { it.id } ?: return)
//			.observe(this) { (grades) ->
//				grades.forEach { resultObject ->
//					test.root?.subNodes?.find { it.id == resultObject.nodeId }?.resultObject =
//						resultObject
//				}
//				root().listTestParts.adapter?.notifyDataSetChanged()
//				Log.d("LOG!", "grades: ${Gson().toJson(grades)}")
//			}
	}

	private fun prepareClickListeners() {
		setCollapsible(root().buttonDescription, root().testDescription)
	}

	private fun prepareTestPartsList() {
		root().listTestParts.setAdapter(AdapterTestPart(test.root?.subNodes?.sorted() ?: listOf()))
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
