package com.desk360.livechat.manager

import android.content.Context
import java.lang.ref.WeakReference

object Desk360LiveChat {
    private var context: WeakReference<Context>? = null
    var manager: BaseChatManager? = null
    val title
        get() = LiveChatHelper.settings?.data?.language?.title ?: ""

    fun getContext() = context?.get()

    fun init(context: Context, livechat: BaseChatManager, isActive: (Boolean) -> Unit) {
        this.manager = livechat
        this.context = WeakReference(context)
        manager?.init(context, isActive)
    }

    fun start() {
        context?.get()?.let { manager?.start(it) }
    }
}