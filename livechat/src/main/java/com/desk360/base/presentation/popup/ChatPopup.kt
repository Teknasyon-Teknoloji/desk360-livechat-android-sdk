package com.desk360.base.presentation.popup

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.desk360.base.util.Utils
import com.desk360.livechat.R
import com.desk360.livechat.databinding.LayoutCustomDialogBinding
import com.desk360.livechat.manager.LiveChatHelper

class ChatPopup(
    context: Context,
    private val message: String? = "",
    private val status: Utils.DialogStatus? = null,
    private val cancelable: Boolean = true,
    private val icon: Drawable? = null,
    private val iconBackground: Drawable? = null,
    private val action: String? = null,
    private val clickListener: ((Dialog) -> Unit)?
) : Dialog(context) {

    var binding: LayoutCustomDialogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(
                context
            ), R.layout.layout_custom_dialog, null, false
        )

        binding?.root?.let { setContentView(it) }
        binding?.color = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor

        initUI()
    }

    private fun initUI() {
        setCanceledOnTouchOutside(false)
        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        setCancelable(cancelable)

        iconBackground?.let {
            binding?.imageViewType?.background = it
        }

        binding?.apply {
            imageViewType.let {
                Glide.with(context)
                    .load(
                        when (status) {
                            Utils.DialogStatus.WARNING -> R.drawable.ic_popup_info
                            Utils.DialogStatus.SUCCESS -> R.drawable.ic_popup_success
                            Utils.DialogStatus.CUSTOM -> icon
                            else -> R.drawable.ic_popup_info
                        }
                    )
                    .into(it)
            }

            textViewAction.text = LiveChatHelper.settings?.data?.language?.ok
            textViewMessage.text = message
            textViewAction.setOnClickListener {
                if (clickListener != null) {
                    clickListener.invoke(this@ChatPopup)
                } else {
                    dismiss()
                }
            }
        }
    }

    override fun show() {
        if (message?.isNotEmpty() == true)
            super.show()
    }

    class Builder(private var context: Context) {
        private var message: String? = ""
        private var cancelable: Boolean = true
        private var status: Utils.DialogStatus? = null
        private var icon: Drawable? = null
        private var iconBackground: Drawable? = null
        private var action: String? = null
        private var clickListener: ((Dialog) -> Unit)? = null

        fun setMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun setStatus(status: Utils.DialogStatus?): Builder {
            this.status = status
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setIcon(icon: Drawable?): Builder {
            this.icon = icon
            return this
        }

        fun setIconBackground(background: Drawable?): Builder {
            this.iconBackground = background
            return this
        }

        fun setAction(action: String?): Builder {
            this.action = action
            return this
        }

        fun setCallbackPositiveButtonClick(clickListener: (Dialog) -> Unit): Builder {
            this.clickListener = clickListener
            return this
        }

        fun build() =
            ChatPopup(
                context,
                message,
                status,
                cancelable,
                icon,
                iconBackground,
                action,
                clickListener
            )
    }
}