package com.desk360.livechat.data.mapper

import com.desk360.base.domain.mapper.FirebaseMapper
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.model.firebase.SessionModel
import com.desk360.livechat.manager.LiveChatHelper
import java.util.*

class SessionMapper : FirebaseMapper<SessionModel, HeaderChatScreenModel?>() {

    override fun map(from: SessionModel?): HeaderChatScreenModel? {
        if (from == null)
            return null

        return HeaderChatScreenModel(
            id = from.receiver.toString(),
            isOffline = from.receiver_status?.toLowerCase(Locale.getDefault()) == "offline",
            companyLogo = from.avatar,
            isAvatarExists = from.avatar?.isNotEmpty() == true || LiveChatHelper.settings?.data?.chatbot == true,
            title = if (LiveChatHelper.settings?.data?.chatbot == true) "Chat Bot" else from.receiver_name,
            initial = getInitial(from.receiver_name),
            titleColor = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
            titleBackgroundColor = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor
        )
    }

    private fun getInitial(receiverName: String?): String? {
        return if (receiverName?.isNotEmpty() == true) {
            var initial = ""
            receiverName.split(' ').forEachIndexed { index, s ->
                if (index < 2) {
                    initial += s.substring(0, 1)
                }
            }

            return initial.toUpperCase(Locale.getDefault())
        } else null
    }
}