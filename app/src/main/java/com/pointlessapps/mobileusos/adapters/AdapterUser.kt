package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.find

class AdapterUser : AdapterSimple<User>(mutableListOf()) {

	private val wholeList = mutableListOf(*list.toTypedArray())

	private lateinit var textName: AppCompatTextView
	private lateinit var imageProfile: CircleImageView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId() = R.layout.list_item_user
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.userName)
		imageProfile = root.find(R.id.userProfileImg)
	}

	override fun onBind(root: View, position: Int) {
		root.find<View>(R.id.bg).setOnClickListener {
			onClickListener?.invoke(list[position])
		}

		textName.text = list[position].name()

		list[position].photoUrls?.values?.firstOrNull()?.also {
			Picasso.get().load(it).into(imageProfile)
		}
	}

	override fun update(list: List<User>) {
		val sortedList = list.sorted()
		wholeList.apply {
			clear()
			addAll(sortedList)
		}
		super.update(sortedList)
	}
}