package com.pointlessapps.mobileusos.adapters

import com.pointlessapps.mobileusos.databinding.ListItemUserBinding
import com.pointlessapps.mobileusos.models.User
import com.squareup.picasso.Picasso

class AdapterUser :
	AdapterCore<User, ListItemUserBinding>(mutableListOf(), ListItemUserBinding::class.java) {

	init {
		setHasStableIds(true)
	}

	override fun isCollapsible() = true

	override fun onCreate(binding: ListItemUserBinding) {
		if (onClickListener == null) {
			binding.root.isClickable = false
			binding.root.setRippleColorResource(android.R.color.transparent)
		}
	}

	override fun onBind(binding: ListItemUserBinding, position: Int) {
		binding.userName.text = list[position].name()

		list[position].photoUrls?.values?.firstOrNull()?.also {
			Picasso.get().load(it).into(binding.userProfileImg)
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
