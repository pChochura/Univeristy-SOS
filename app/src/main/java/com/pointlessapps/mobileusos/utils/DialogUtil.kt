package com.pointlessapps.mobileusos.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import kotlin.math.min

class DialogUtil<Binding : ViewBinding> private constructor(
	private val activity: Context,
	private val bindingClass: Class<Binding>,
	private val windowSize: IntArray
) {

	companion object {
		const val UNDEFINED_WINDOW_SIZE = Integer.MAX_VALUE

		fun <Binding : ViewBinding> create(
			context: Context,
			bindingClass: Class<Binding>,
			callback: Dialog.(Binding) -> Unit,
			vararg windowSize: Int = intArrayOf(UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
		) {
			val dialog = DialogUtil(context, bindingClass, windowSize)
			dialog.makeDialog { binding, dialogView ->
				callback.invoke(dialogView, binding)
			}
		}

		fun <Binding : ViewBinding> create(
			statefulDialog: StatefulDialog<Binding>,
			context: Context,
			bindingClass: Class<Binding>,
			callback: StatefulDialog<Binding>.(Binding) -> Unit,
			vararg windowSize: Int = intArrayOf(UNDEFINED_WINDOW_SIZE, ViewGroup.LayoutParams.WRAP_CONTENT)
		) {
			val dialog = DialogUtil(context, bindingClass, windowSize)
			dialog.makeDialog { binding, dialogView ->
				callback.invoke(statefulDialog.apply {
					this.binding = binding
					this.dialog = dialogView
				}, binding)
				if (statefulDialog.showToggled) {
					statefulDialog.toggle()
				}
			}
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun makeDialog(callback: (Binding, Dialog) -> Unit) {
		val dialog = Dialog(activity)
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		dialog.window?.also {
			it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
			it.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
			it.attributes?.dimAmount = 0.5f
		}
		val size = Utils.getScreenSize()
		val width =
			if (windowSize.isNotEmpty() && windowSize.first() != UNDEFINED_WINDOW_SIZE) windowSize[0]
			else min(350.dp, size.x - 150)
		val height =
			if (windowSize.size > 1 && windowSize[1] != UNDEFINED_WINDOW_SIZE) windowSize[1]
			else min(500.dp, size.y - 150)

		val binding = bindingClass.getMethod(
			"inflate",
			LayoutInflater::class.java,
			ViewGroup::class.java,
			Boolean::class.java
		).invoke(null, LayoutInflater.from(activity), null, false) as Binding

		dialog.setContentView(
			binding.root,
			ViewGroup.LayoutParams(width, height)
		)
		callback.invoke(binding, dialog)
		if (!dialog.isShowing)
			dialog.show()
	}

	abstract class StatefulDialog<Binding : ViewBinding> {
		lateinit var dialog: Dialog
		lateinit var binding: Binding
		var showToggled = false
		var toggled = false

		abstract fun toggle()
	}
}
