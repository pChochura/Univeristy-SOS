package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import org.jetbrains.anko.find

class AdapterQuestion : AdapterSimple<Survey.Question>(mutableListOf()) {

	lateinit var onCheckedListener: (String, String) -> Unit

	private lateinit var textName: AppCompatTextView
	private lateinit var listSubQuestions: RecyclerView

	override fun getLayoutId(viewType: Int) = R.layout.list_item_question

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.questionName)
		listSubQuestions = root.find(R.id.listSubQuestions)
	}

	override fun onBind(root: View, position: Int) {
		textName.text = Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		listSubQuestions.apply {
			adapter = AdapterSubQuestion(list[position].subQuestions ?: listOf()).apply {
				onCheckedListener = this@AdapterQuestion.onCheckedListener
			}
			layoutManager =
				UnscrollableLinearLayoutManager(root.context, RecyclerView.VERTICAL, false)
		}
	}
}

class AdapterSubQuestion(subQuestions: List<Survey.Question>) :
	AdapterSimple<Survey.Question>(subQuestions.toMutableList()) {

	lateinit var onCheckedListener: (String, String) -> Unit

	private lateinit var textName: AppCompatTextView
	private lateinit var listSubSubQuestions: RecyclerView

	override fun getLayoutId(viewType: Int) = R.layout.list_item_sub_question

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.subQuestionName)
		listSubSubQuestions = root.find(R.id.listSubSubQuestions)
	}

	override fun onBind(root: View, position: Int) {
		if (list[position].subQuestions.isNullOrEmpty()) {
			textName.isGone = true
		}
		textName.text = "%s. %s".format(
			list[position].number,
			Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		)

		listSubSubQuestions.apply {
			adapter = AdapterSubSubQuestion(
				list[position].subQuestions?.takeIf { it.isNotEmpty() }?.toMutableList()
					?: mutableListOf(list[position])
			).apply {
				onCheckedListener = this@AdapterSubQuestion.onCheckedListener
			}
			layoutManager =
				UnscrollableLinearLayoutManager(root.context, RecyclerView.VERTICAL, false)
		}
	}
}

class AdapterSubSubQuestion(subSubQuestions: List<Survey.Question>) :
	AdapterSimple<Survey.Question>(subSubQuestions.toMutableList()) {

	lateinit var onCheckedListener: (String, String) -> Unit

	private lateinit var textName: AppCompatTextView
	private lateinit var answersGroup: RadioGroup

	override fun getLayoutId(viewType: Int) = R.layout.list_item_sub_sub_question

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.subSubQuestionName)
		answersGroup = root.find(R.id.answersGroup)
	}

	override fun onBind(root: View, position: Int) {
		textName.text = "%s. %s".format(
			list[position].number,
			Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		)

		answersGroup.apply {
			LayoutInflater.from(root.context).also {
				list[position].possibleAnswers?.forEach { answer ->
					addView(it.inflate(R.layout.list_item_question_answer, null, false)
						.apply {
							(this as RadioButton).text =
								Utils.stripHtmlTags(answer.displayTextHtml.toString())
							setOnCheckedChangeListener { _, _ ->
								onCheckedListener(list[position].id, answer.id)
							}
						})
				}
			}
		}
	}
}
