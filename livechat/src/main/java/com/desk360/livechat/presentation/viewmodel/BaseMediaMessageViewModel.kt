package com.desk360.livechat.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.manager.FileManager

abstract class BaseMediaMessageViewModel : BaseMessageViewModel<Message>() {
    val isDownload: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isUploading: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isProcessing: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val isPreview: MutableLiveData<Boolean> by lazy {
        MutableLiveData(false)
    }

    val percentage: MutableLiveData<Int> by lazy {
        MutableLiveData(0)
    }

    override fun handleMessage(message: Message) {
        super.handleMessage(message)
        percentage.value = message.percentage
        isUploading.value = message.status == ChatUtils.Status.SENDING
        isProcessing.value = message.status == ChatUtils.Status.SENDING
        message.attachments?.firstOrNull()?.let { attachment ->
            val fileName = if (message.isMine) {
                val nameAndExtension = attachment.name.toString().split('.')
                "${nameAndExtension[0]}_${message.time}.${nameAndExtension[1]}"
            } else attachment.name.toString()

            isDownload.value =
                FileManager.isExists(fileName) || isProcessing.value == true || isError.value == true
        }

        isPreview.value =
            isDownload.value == true && isProcessing.value != true && isError.value != true
    }
}