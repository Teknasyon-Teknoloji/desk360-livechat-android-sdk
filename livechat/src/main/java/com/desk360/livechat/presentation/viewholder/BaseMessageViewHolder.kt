package com.desk360.livechat.presentation.viewholder

import android.view.View
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.data.model.chat.Message
import com.desk360.livechat.manager.LiveChatFirebaseHelper

abstract class BaseMessageViewHolder(itemView: View) :
    BaseViewHolder<Message>(itemView) {

    open fun bindTo(message: Message, position: Int) {
        bindTo(message)

        if (!message.isMine && message.status != ChatUtils.Status.SEEN) {
            LiveChatFirebaseHelper.database?.getReference("messages/${LiveChatFirebaseHelper.userId}/${message.receiverId}/${message.id}/status")
                ?.setValue("read")
        }
    }
}