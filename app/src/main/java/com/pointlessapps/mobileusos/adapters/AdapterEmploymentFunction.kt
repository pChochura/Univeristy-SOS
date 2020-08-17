package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.User
import org.jetbrains.anko.find

class AdapterEmploymentFunction : AdapterSimple<User.EmploymentFunction>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textFunction: AppCompatTextView
	private lateinit var textFaculty: AppCompatTextView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_user_employment_function
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textFunction = root.find(R.id.textFunction)
		textFaculty = root.find(R.id.textFaculty)
	}

	override fun onBind(root: View, position: Int) {
		textFunction.text = list[position].function?.toString()
		textFaculty.text = list[position].faculty.name?.toString()
	}

	override fun update(list: List<User.EmploymentFunction>) {
		wholeList.apply {
			clear()
			addAll(list)
		}
		super.update(list)
	}
}
