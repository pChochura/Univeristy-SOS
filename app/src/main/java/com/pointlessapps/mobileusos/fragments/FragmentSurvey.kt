package com.pointlessapps.mobileusos.fragments

import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.adapters.AdapterQuestion
import com.pointlessapps.mobileusos.models.Survey
import com.pointlessapps.mobileusos.utils.DialogUtil
import com.pointlessapps.mobileusos.utils.UnscrollableLinearLayoutManager
import com.pointlessapps.mobileusos.viewModels.ViewModelUser
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_message.*
import kotlinx.android.synthetic.main.fragment_survey.view.*

class FragmentSurvey(private var survey: Survey) : FragmentBase() {

	private val viewModelUser by viewModels<ViewModelUser>()
	private val answers = mutableMapOf<String, String>()

	override fun getLayoutId() = R.layout.fragment_survey

	override fun created() {
		prepareData()
		prepareQuestionsList()
		prepareClickListeners()

		viewModelUser.getSurveysById(survey.id).observe(this) { (survey) ->
			if (survey === null) {
				return@observe
			}

			this.survey = survey
			root().horizontalProgressBar.isRefreshing = true
		}.onFinished {
			prepareData()
			prepareQuestions()
			root().horizontalProgressBar.isRefreshing = false
		}
	}

	private fun prepareData() {
		root().lecturerName.text = survey.lecturer?.name()
		root().courseName.text = requireContext().getString(
			R.string.course_info_general,
			survey.group?.courseName,
			survey.group?.classType
		)
		survey.lecturer?.photoUrls?.values?.firstOrNull()?.also {
			Picasso.get().load(it).into(root().lecturerProfileImg)
		}
	}

	private fun prepareQuestions() {
		(root().listQuestions.adapter as? AdapterQuestion)?.update(survey.questions ?: return)
	}

	private fun prepareQuestionsList() {
		root().listQuestions.apply {
			adapter = AdapterQuestion().apply {
				onCheckedListener = { questionId, answerId ->
					answers[questionId] = answerId
				}
			}
			layoutManager =
				UnscrollableLinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
		}
	}

	private fun prepareClickListeners() {
		root().buttonSend.setOnClickListener {
			if (answers.keys.size < survey.numberOfAnswers) {
				DialogUtil.create(requireContext(), R.layout.dialog_message, { dialog ->
					dialog.messageMain.setText(R.string.complete_all_answers)
					dialog.messageSecondary.setText(R.string.complete_all_answers_description)
					dialog.buttonSecondary.isGone = true

					dialog.buttonPrimary.setText(android.R.string.ok)
					dialog.buttonPrimary.setOnClickListener { dialog.dismiss() }
				}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)

				return@setOnClickListener
			}

			val comment = root().inputComment.text?.toString()
			viewModelUser.fillOutSurvey(survey.id, answers, comment)

			DialogUtil.create(requireContext(), R.layout.dialog_message, { dialog ->
				dialog.messageMain.setText(R.string.thank_you)
				dialog.messageSecondary.setText(R.string.thank_you_fill_survey)
				dialog.buttonSecondary.isGone = true

				dialog.buttonPrimary.setText(android.R.string.ok)
				dialog.buttonPrimary.setOnClickListener {
					dialog.dismiss()
					onBackPressedListener?.invoke()
				}
			}, DialogUtil.UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
		}
	}
}
