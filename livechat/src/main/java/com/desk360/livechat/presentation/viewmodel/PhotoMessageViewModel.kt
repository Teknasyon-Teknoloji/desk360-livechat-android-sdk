package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.model.chat.Message

class PhotoMessageViewModel : BaseMediaMessageViewModel() {

    val url: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    override fun handleMessage(message: Message) {
        super.handleMessage(message)
        url.value = message.attachments?.firstOrNull()?.url
    }
}