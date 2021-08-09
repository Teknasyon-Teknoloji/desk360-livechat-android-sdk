package com.desk360.livechat

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.manager.FileManager
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.R

object BindingExt {

    @JvmStatic
    @BindingAdapter("bind:visibility")
    fun setVisibility(view: View, visible: Boolean?) {
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("bind:textColor")
    fun setTextColor(textView: TextView, color: String?) {
        if (!color.isNullOrEmpty())
            textView.setTextColor(Color.parseColor(color))
    }

    @JvmStatic
    @BindingAdapter("bind:background")
    fun setBackgroundColor(view: View, color: String?) {
        if (color?.isNotEmpty() == true)
            view.setBackgroundColor(Color.parseColor(color))
    }

    @JvmStatic
    @BindingAdapter("bind:drawableTint")
    fun setDrawableTint(textView: TextView, color: String?) {
        color?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    textView.compoundDrawables[0].setTint(Color.parseColor(color))
                } catch (ex: Exception) {

                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:tint")
    fun setTint(imageView: ImageView, color: String?) {
        color?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    imageView.imageTintList = ColorStateList.valueOf(Color.parseColor(color))
                } catch (ex: Exception) {

                }
            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:backgroundTint")
    fun setBackgroundTint(view: View, color: String?) {
        if (!color.isNullOrEmpty() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(color))
        }
    }

    @JvmStatic
    @BindingAdapter("bind:src")
    fun setImage(view: ImageView, imageUrl: String?) {
        imageUrl?.let {
            Glide.with(view)
                .load(imageUrl)
                .circleCrop()
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bind:avatar")
    fun setAvatar(view: ImageView, imageUrl: String?) {
        if (LiveChatHelper.settings?.data?.chatbot == true) {
            Glide.with(view)
                .load(R.drawable.ic_chatbot)
                .circleCrop()
                .into(view)
        } else {
            Glide.with(view)
                .load(imageUrl)
                .circleCrop()
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bind:photo")
    fun setPhoto(view: ImageView, imageUrl: String?) {
        imageUrl?.let {
            Glide.with(view)
                .load(imageUrl)
                .override(220, 130)
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bind:extension")
    fun setExtension(view: ImageView, extension: String?) {
        extension?.let {

            val drawable = when (extension) {
                FileManager.Extensions.XLSX -> R.drawable.ic_ext_xlsx
                FileManager.Extensions.XLS -> R.drawable.ic_ext_xls
                FileManager.Extensions.DOCX -> R.drawable.ic_ext_docx
                FileManager.Extensions.DOC -> R.drawable.ic_ext_doc
                FileManager.Extensions.PDF -> R.drawable.ic_ext_pdf
                else -> -1
            }

            if (drawable != -1)
                Glide.with(view)
                    .load(drawable)
                    .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bind:drawable")
    fun setDrawable(view: ImageView, drawable: Int?) {
        drawable?.let {
            Glide.with(view)
                .load(drawable)
                .into(view)
        }
    }

    @JvmStatic
    @BindingAdapter("bind:setMessageStatus")
    fun setMessageStatus(view: ImageView, status: Int) {
        Glide.with(view)
            .load(
                when (status) {
                    ChatUtils.Status.FORWARDED -> R.drawable.ic_blue_tick_forwarded
                    ChatUtils.Status.SEEN -> R.drawable.ic_blue_tick_seen
                    else -> R.drawable.ic_blue_tick_sent
                }
            )
            .into(view)
    }

    @JvmStatic
    @BindingAdapter("bind:boxStrokeColor")
    fun setBoxStrokeColor(textInputLayout: TextInputLayout, color: String?) {
        color?.let {
            try {
                textInputLayout.boxStrokeColor = Color.parseColor(color)
            } catch (ex: Exception) {

            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:hintTextColor")
    fun setHintTextColor(textInputLayout: TextInputLayout, color: String?) {
        color?.let {
            try {
                textInputLayout.hintTextColor = ColorStateList.valueOf(Color.parseColor(color))
            } catch (ex: Exception) {

            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:indeterminateTint")
    fun setIndeterminateTint(progressBar: ProgressBar, color: String?) {
        color?.let {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    progressBar.indeterminateTintList =
                        ColorStateList.valueOf(Color.parseColor(color))
                }
            } catch (ex: Exception) {

            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:progressTint")
    fun setProgressTint(progressBar: ProgressBar, color: String?) {
        color?.let {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    progressBar.progressTintList =
                        ColorStateList.valueOf(Color.parseColor(color))
                }
            } catch (ex: Exception) {

            }
        }
    }

    @JvmStatic
    @BindingAdapter("bind:marginToMine")
    fun setMarginToMine(view: View, isMine: Boolean?) {
        val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
        when (isMine) {
            true -> {
                layoutParams.setMargins(30, 0, 0, 0)

            }
            else -> {
                layoutParams.setMargins(0, 0, 30, 0)
            }
        }
        view.requestLayout()
    }

    fun <T : ViewDataBinding> Activity.binding(
        @LayoutRes resId: Int
    ): Lazy<T> = lazy { DataBindingUtil.setContentView(this, resId) }
}