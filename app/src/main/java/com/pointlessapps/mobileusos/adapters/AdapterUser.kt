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

	override fun onCreate(root: View, position: Int) {
		super.onCreate(root, position)
		textName = root.find(R.id.userName)
		imageProfile = root.find(R.id.userProfileImg)
	}

	override fun onBind(root: View, position: Int) {
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
