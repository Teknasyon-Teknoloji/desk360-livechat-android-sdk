package com.desk360.base.presentation.component

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ProgressDialogViewBinding

class CustomProgressDialog {


    lateinit var dialog: CustomDialog

    var binding: ProgressDialogViewBinding? = null

    fun show(context: Context): Dialog {
        return show(context, null)
    }

    fun show(context: Context, title: CharSequence?): Dialog {
        val inflater = (context as Activity).layoutInflater
        binding = DataBindingUtil.inflate(inflater, R.layout.progress_dialog_view, null, false)
        if (title != null) {
            binding?.cpTitle?.text = title
        }

        // Card Color
        binding?.cpCardview?.setCardBackgroundColor(Color.parseColor("#70000000"))

        // Progress Bar Color
        binding?.cpPbar?.indeterminateDrawable?.let { setColorFilter(it, ResourcesCompat.getColor(context.resources, R.color.colorPrimary, null)) }

        // Text Color
        binding?.cpTitle?.setTextColor(Color.WHITE)

        dialog = CustomDialog(context)
        binding?.root?.let { dialog.setContentView(it) }
        dialog.show()
        return dialog
    }

    private fun setColorFilter(drawable: Drawable, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            drawable.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
        } else {
            @Suppress("DEPRECATION")
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    class CustomDialog(context: Context) : Dialog(context, R.style.CustomDialogTheme) {
        init {
            // Set Semi-Transparent Color for Dialog Background
            //window?.decorView?.rootView?.setBackgroundResource(R.color.bg_toolbar_header)
            /*window?.decorView?.setOnApplyWindowInsetsListener { _, insets ->
                insets.consumeSystemWindowInsets()
            }*/
        }
        fun hideDialog() {
            if (isShowing) dismiss()
        }
        override fun onBackPressed() {}
    }
}