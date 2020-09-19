package com.pointlessapps.mobileusos.adapters

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.card.MaterialCardView
import com.pointlessapps.mobileusos.R
import com.pointlessapps.mobileusos.models.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.anko.find

class AdapterUser : AdapterSimple<User>(mutableListOf()) {

	private lateinit var textName: AppCompatTextView
	private lateinit var imageProfile: CircleImageView

	init {
		setHasStableIds(true)
	}

	override fun getLayoutId(viewType: Int) = R.layout.list_item_user
	override fun isCollapsible() = true

	override fun onCreate(root: View) {
		super.onCreate(root)
		textName = root.find(R.id.userName)
		imageProfile = root.find(R.id.userProfileImg)

		if (onClickListener == null) {
			root.find<View>(R.id.bg).isClickable = false
			root.find<MaterialCardView>(R.id.bg).setRippleColorResource(android.R.color.transparent)
		}
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
		list.forEach {
			val found = this.list.find { user -> it.id == user.id }
			if (found == null || found.titles != it.titles) {
				super.update(list.sorted())
				return@forEach
			}
		}
	}
}
