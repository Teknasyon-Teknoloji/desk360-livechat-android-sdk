package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.data.model.chat.getTime
import com.desk360.livechat.manager.LiveChatHelper

// base message properties: incoming, outgoing, time, blue tick...
abstract class BaseMessageViewModel<T : Message> : BaseViewModel() {
    private var myMessage: T? = null

    val messageTextColor: MutableLiveData<String> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.chat?.messageTextColor)
    }

    val backgroundColor: MutableLiveData<String> by lazy {
        MutableLiveData(LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor)
    }

    val isMine: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val time: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    val status: MutableLiveData<Int> by lazy {
        MutableLiveData(ChatUtils.Status.SENT)
    }

    val isError: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val body: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    open fun handleMessage(message: T) {
        backgroundColor.value =
            if (message.isMine) LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor else
                LiveChatHelper.settings?.data?.config?.chat?.messageBackgroundColor

        messageTextColor.value =
            if (message.isMine) LiveChatHelper.settings?.data?.config?.general?.headerTitleColor else
                LiveChatHelper.settings?.data?.config?.chat?.messageTextColor

        myMessage = message
        isError.value = message.status == ChatUtils.Status.ERROR
        body.value = message.body
        isMine.value = message.isMine
        time.value = message.getTime()
        status.value = message.status
    }
}