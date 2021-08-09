package com.desk360.base.presentation.component

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.desk360.livechat.BindingExt
import com.desk360.livechat.manager.LiveChatHelper

class LiveChatFloatingActionButton : FloatingActionButton {

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        BindingExt.setBackgroundColor(
            this,
            LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
        )

        LiveChatHelper.settings?.data?.config?.general?.headerTitleColor?.let { color ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    imageTintList = ColorStateList.valueOf(Color.parseColor(color))
                } catch (ex: Exception) {

                }
            }
        }
    }
}