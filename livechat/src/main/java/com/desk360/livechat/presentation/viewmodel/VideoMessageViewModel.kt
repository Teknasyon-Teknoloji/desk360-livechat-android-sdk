package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.model.chat.Message

class VideoMessageViewModel : BaseMediaMessageViewModel() {

    val fileName: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    override fun handleMessage(message: Message) {
        super.handleMessage(message)
        fileName.value = message.attachments?.firstOrNull()?.name
    }
}