package com.desk360.livechat.manager

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.desk360.livechat.R
import com.desk360.livechat.data.model.chatsettings.ChatSettingsResponse

object LiveChatHelper {
    var settings: ChatSettingsResponse? = null
    var isOffline = false

    fun getStatusColor() =
        if (!settings?.data?.config?.general?.backgroundHeaderColor.isNullOrEmpty()) Color.parseColor(
            settings?.data?.config?.general?.backgroundHeaderColor
        ) else R.color.colorPrimary

    fun closeKeyboard(context: Context, view: View?) {
        view?.let {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                view.windowToken,
                0
            )
        }
    }
}