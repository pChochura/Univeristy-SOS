package com.pointlessapps.mobileusos.fragments

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterQuestion
import com.pointlessapps.mobileusos.databinding.DialogLoadingBinding
import com.pointlessapps.mobileusos.databinding.DialogMessageBinding
import com.pointlessapps.mobileusos.databinding.FragmentSurveyBinding
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.utils.Utils
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso

class FragmentSurvey(private var survey: Survey) :
	FragmentCoreImpl<FragmentSurveyBinding>(FragmentSurveyBinding::class.java) {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val answers = mutableMapOf<String, MutableMap<String, Any?>>()

	override fun created() {
		prepareData()
		prepareQuestionsList()
		prepareClickListeners()

		viewModelUser.getSurveysById(survey.id).observe(this) { (survey) ->
			if (survey === null) {
				return@observe
			}

			this.survey = survey
			binding().horizontalProgressBar.isRefreshing = true
		}.onFinished {
			if (it !== null) {
				showErrorDialog()
				return@onFinished
			}

			prepareData()
			prepareQuestions()
			binding().horizontalProgressBar.isRefreshing = false
			binding().buttonSend.isVisible = true
			binding().sectionComment.isVisible = survey.hasFinalComment
		}
	}

	private fun prepareData() {
		if (survey.surveyType == Survey.SurveyType.Course) {
			binding().surveyHeadline.isGone = true
			binding().sectionCourse.isGone = false
			binding().lecturerName.text = survey.lecturer?.name()
			binding().courseName.text = requireContext().getString(
				R.string.course_info_general,
				survey.group?.courseName,
				survey.group?.classType
			)
			survey.lecturer?.photoUrls?.values?.firstOrNull()?.also {
				Picasso.get().load(it).into(binding().lecturerProfileImg)
			}
		} else {
			binding().surveyHeadline.text = Utils.stripHtmlTags(
				survey.headlineHtml ?: requireContext().getString(
					R.string.course_info_general,
					survey.faculty?.name.toString(),
					survey.id.substringAfter("|").substringBeforeLast("|")
				)
			)
			binding().sectionCourse.isGone = true
			binding().surveyHeadline.isGone = false
		}
	}

	private fun prepareQuestions() {
		(binding().listQuestions.adapter as? AdapterQuestion)?.update(survey.questions ?: return)
	}

	private fun prepareQuestionsList() {
		binding().listQuestions.apply {
			adapter = AdapterQuestion().apply {
				onCheckedListener = { questionId, answerId ->
					if (answers[questionId] == null) {
						answers[questionId] = mutableMapOf()
					}
					answers[questionId]!!["answers"] = listOf(answerId)
				}
				onCommentChangedListener = { questionId, comment ->
					if (answers[questionId] == null) {
						answers[questionId] = mutableMapOf()
					}
					answers[questionId]!!["comment"] = comment
				}
			}
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareClickListeners() {
		binding().buttonSend.setOnClickListener {
			if (answers.keys.size < survey.numberOfAnswers) {
				DialogUtil.create(requireContext(), DialogMessageBinding::class.java, { dialog ->
					dialog.messageMain.setText(R.string.complete_all_answers)
					dialog.messageSecondary.setText(R.string.complete_all_answers_description)

					dialog.buttonPrimary.setText(android.R.string.ok)
					dialog.buttonPrimary.setOnClickListener {
						dismiss()
						sendSurvey()
					}
					dialog.buttonSecondary.setText(R.string.cancel)
					dialog.buttonSecondary.setOnClickListener { dismiss() }
				}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
			} else {
				sendSurvey()
			}
		}
	}

	private fun sendSurvey() {
		DialogUtil.create(object : DialogUtil.StatefulDialog<DialogLoadingBinding>() {
			override fun toggle() {
				binding.progressBar.isVisible = false
				binding.messageSecondary.isGone = false
				binding.buttonPrimary.isGone = false
			}
		}, requireContext(), DialogLoadingBinding::class.java, { dialog ->
			this.dialog.setCancelable(false)

			dialog.messageMain.setText(R.string.saving_survey)
			dialog.progressBar.isGone = false
			dialog.buttonPrimary.isGone = true
			dialog.buttonSecondary.isGone = true
			dialog.messageSecondary.isGone = true

			dialog.buttonPrimary.setOnClickListener {
				this.dialog.dismiss()
				onForceGoBack?.invoke()
			}

			viewModelUser.fillOutSurvey(
				survey.id,
				answers.mapValues {
					mapOf(
						"answers" to it.value["answers"],
						"comment" to it.value.getOrDefault("comment", null)
					)
				},
				binding().inputComment.text?.takeIf { survey.hasFinalComment }?.toString()
			).onFinished {
				toggle()
				if (it !== null) {
					dialog.messageMain.setText(R.string.oops)
					dialog.messageSecondary.setText(R.string.something_went_wrong)
				} else {
					dialog.messageMain.setText(R.string.all_done)
					dialog.messageSecondary.setText(R.string.thank_you_fill_survey)
				}
			}
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}

	private fun showErrorDialog() {
		DialogUtil.create(requireContext(), DialogMessageBinding::class.java, { dialog ->
			dialog.messageMain.setText(R.string.oops)
			dialog.messageSecondary.setText(R.string.something_went_wrong)
			dialog.buttonSecondary.isGone = true

			dialog.buttonPrimary.setText(android.R.string.ok)
			dialog.buttonPrimary.setOnClickListener {
				dismiss()
				onForceGoBack?.invoke()
			}
			setOnCancelListener {
				dismiss()
				onForceGoBack?.invoke()
			}
		}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
	}
}
