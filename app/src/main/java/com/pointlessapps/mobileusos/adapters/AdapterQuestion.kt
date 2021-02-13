package com.pointlessapps.mobileusos.adapters

import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.databinding.ListItemQuestionAnswerBinding
import com.pointlessapps.mobileusos.databinding.ListItemQuestionBinding
import com.pointlessapps.mobileusos.databinding.ListItemSubQuestionBinding
import com.pointlessapps.mobileusos.databinding.ListItemSubSubQuestionBinding
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import java.util.*

class AdapterQuestion : AdapterCore<Survey.Question, ListItemQuestionBinding>(
	mutableListOf(),
	ListItemQuestionBinding::class.java
) {

	lateinit var onCheckedListener: (String, String) -> Unit
	lateinit var onCommentChangedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemQuestionBinding, position: Int) {
		binding.questionName.text = Utils.stripHtmlTags(list[position].displayTextHtml.toString())
		binding.listSubQuestions.apply {
			val subQuestions = list[position].subQuestions?.toMutableList() ?: mutableListOf()
			if (list[position].possibleAnswers?.isNotEmpty() == true) {
				subQuestions.add(
					0, Survey.Question(
						displayTextHtml = null,
						subQuestions = null,
						level = 1,
						number = "${list[position].number}.0",
						id = list[position].id,
						allowComment = false,
						commentLength = null,
						possibleAnswers = list[position].possibleAnswers
					)
				)
			}
			adapter = AdapterSubQuestion(subQuestions).apply {
				onCheckedListener = this@AdapterQuestion.onCheckedListener
				onCommentChangedListener = this@AdapterQuestion.onCommentChangedListener
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
	lateinit var onCommentChangedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemSubQuestionBinding, position: Int) {
		if (list[position].subQuestions.isNullOrEmpty()) {
			binding.subQuestionName.isGone = true
		}
		if (list[position].displayTextHtml?.isEmpty() != false) {
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
				onCommentChangedListener = this@AdapterSubQuestion.onCommentChangedListener
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
	lateinit var onCommentChangedListener: (String, String) -> Unit

	override fun onBind(binding: ListItemSubSubQuestionBinding, position: Int) {
		if (list[position].allowComment) {
			binding.inputComment.isVisible = true
			binding.inputComment.addTextChangedListener {
				onCommentChangedListener(list[position].id, it.toString())
			}
		}
		if (list[position].displayTextHtml?.isEmpty() != false) {
			binding.subSubQuestionName.isGone = true
		}
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
