package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.data.model.media.getExtension

class DocumentMessageViewModel : BaseMediaMessageViewModel() {

    val name: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    val extension: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }

    override fun handleMessage(message: Message) {
        super.handleMessage(message)
        message.attachments?.firstOrNull()?.let { attachment ->
            name.value = attachment.name
            extension.value = attachment.getExtension()
        }
    }
}