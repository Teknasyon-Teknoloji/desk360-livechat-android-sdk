package com.desk360.livechat.data.model.chat

import android.annotation.SuppressLint
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.model.BaseModel
import com.desk360.livechat.data.model.media.MediaModel
import java.text.SimpleDateFormat

data class Message(
    var id: String? = null,
    var senderName: String = "",
    var receiverId: String? = "",
    var receiverName: String = "",
    var time: Long? = null,
    var isMine: Boolean = true,
    @ChatUtils.Message.Type var type: Int = ChatUtils.Message.TEXT,
    @ChatUtils.Status.Type var status: Int = ChatUtils.Status.SENDING,
    var isProcessing: Boolean = false,
    var isError: Boolean = false,
    val body: String = "",
    var attachments: List<MediaModel>? = null,
    var percentage: Int? = 0
) : BaseModel()

@SuppressLint("SimpleDateFormat")
fun Message.getTime(): String {
    val timeFormat = SimpleDateFormat("HH:mm")
    return timeFormat.format(time)
}

