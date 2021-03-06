package com.desk360.base.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.desk360.base.presentation.menu.MenuItem
import com.desk360.base.presentation.popup.ChatPopup
import com.desk360.livechat.R
import com.desk360.livechat.manager.LiveChatHelper
import java.net.ConnectException
import java.net.UnknownHostException


object Utils {
    const val DEFAULT_LANGUAGE_CODE = "en"

    const val DEFAULT_SOURCE = "Android"

    fun isEmailValid(email: String) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun handleError(context: Context, th: Throwable) {
        val message = when (th) {
            is ConnectException -> LiveChatHelper.settings?.data?.language?.sdkCheckYourConnection
            is UnknownHostException -> LiveChatHelper.settings?.data?.language?.sdkCheckYourConnection
            else -> LiveChatHelper.settings?.data?.language?.sdkEerrorHasOccurred
        }

        if (th is ConnectException || th is UnknownHostException) {
            ChatPopup.Builder(context)
                .setStatus(DialogStatus.CUSTOM)
                .setIcon(ContextCompat.getDrawable(context, R.drawable.ic_internet))
                .setIconBackground(ContextCompat.getDrawable(context, R.drawable.ic_warning))
                .setAction(LiveChatHelper.settings?.data?.language?.error)
        } else {
            ChatPopup.Builder(context)
                .setStatus(DialogStatus.ERROR)
        }
            .setMessage(message)
            .setCallbackPositiveButtonClick { dialog ->
                dialog.dismiss()
            }.build().show()
    }

    fun getBrandLogo() = when {
        LiveChatHelper.settings?.data?.config?.general?.brandLogo?.isNotEmpty() == true -> LiveChatHelper.settings?.data?.config?.general?.brandLogo
        LiveChatHelper.settings?.data?.applicationLogo?.isNotEmpty() == true -> LiveChatHelper.settings?.data?.applicationLogo
        else -> LiveChatHelper.settings?.data?.defaultBrandLogo
    }

    fun getBrandName() = when {
        LiveChatHelper.settings?.data?.config?.general?.brandName?.isNotEmpty() == true -> LiveChatHelper.settings?.data?.config?.general?.brandName
        else -> LiveChatHelper.settings?.data?.applicationName
    }

    fun getAgentAvatar(avatar: String?) = when {
        LiveChatHelper.settings?.data?.config?.general?.agentPictureStatus == true && avatar?.isNotEmpty() == true -> avatar
        else -> getBrandLogo()
    }

    fun getLiveChatMenus(context: Context): List<MenuItem> {
        val samples = ArrayList<MenuItem>()
        samples.add(
            MenuItem(
                LiveChatMenuItem.END_CHAT,
                ContextCompat.getDrawable(context, R.drawable.ic_close),
                LiveChatHelper.settings?.data?.language?.headerMenuEndChat ?: ""
            )
        )
        return samples
    }

    object LiveChatMenuItem {
        const val END_CHAT = 0
        const val SEND_TRANSCRIPT = 1
    }

    enum class DialogStatus {
        SUCCESS, ERROR, WARNING, INFO, CUSTOM
    }
}