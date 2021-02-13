package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.databinding.ListItemQuestionAnswerBinding
import com.pointlessapps.mobileusos.databinding.ListItemQuestionBinding
import com.pointlessapps.mobileusos.databinding.ListItemSubQuestionBinding
import com.pointlessapps.mobileusos.databinding.ListItemSubSubQuestionBinding
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils

class AdapterQuestion : AdapterCore<Survey.Question, ListItemQuestionBinding>(
	mutableListOf(),
	ListItemQuestionBinding::class.java
) {

	lateinit var onCheckedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemQuestionBinding, position: Int) {
		binding.questionName.text = Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		binding.listSubQuestions.apply {
			adapter = AdapterSubQuestion(list[position].subQuestions ?: listOf()).apply {
				onCheckedListener = this@AdapterQuestion.onCheckedListener
			}
			layoutManager =
				UnscrollableLinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
		}
	}
}

class AdapterSubQuestion(subQuestions: List<Survey.Question>) :
	AdapterCore<Survey.Question, ListItemSubQuestionBinding>(
		subQuestions.toMutableList(),
		ListItemSubQuestionBinding::class.java
	) {

	lateinit var onCheckedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemSubQuestionBinding, position: Int) {
		if (list[position].subQuestions.isNullOrEmpty()) {
			binding.subQuestionName.isGone = true
		}
		binding.subQuestionName.text = "%s. %s".format(
			list[position].number,
			Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		)

		binding.listSubSubQuestions.apply {
			adapter = AdapterSubSubQuestion(
				list[position].subQuestions?.takeIf { it.isNotEmpty() }?.toMutableList()
					?: mutableListOf(list[position])
			).apply {
				onCheckedListener = this@AdapterSubQuestion.onCheckedListener
			}
			layoutManager =
				UnscrollableLinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
		}
	}
}

class AdapterSubSubQuestion(subSubQuestions: List<Survey.Question>) :
	AdapterCore<Survey.Question, ListItemSubSubQuestionBinding>(
		subSubQuestions.toMutableList(),
		ListItemSubSubQuestionBinding::class.java
	) {

	lateinit var onCheckedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemSubSubQuestionBinding, position: Int) {
		binding.subSubQuestionName.text = "%s. %s".format(
			list[position].number,
			Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		)

		binding.answersGroup.apply {
			LayoutInflater.from(context).also {
				list[position].possibleAnswers?.forEach { answer ->
					addView(
						ListItemQuestionAnswerBinding.inflate(
							it,
							null,
							false
						).root.apply {
							text = Utils.stripHtmlTags(answer.displayTextHtml.toString())
							setOnCheckedChangeListener { _, _ ->
								onCheckedListener(list[position].id, answer.id)
							}
						}
					)
				}
			}
		}
	}
}
