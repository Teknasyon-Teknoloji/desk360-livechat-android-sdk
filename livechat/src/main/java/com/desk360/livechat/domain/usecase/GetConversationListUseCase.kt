package com.desk360.livechat.domain.usecase

import com.desk360.base.data.entity.ConversationItem
import com.desk360.base.domain.repository.ConversationRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.ChatUtils
import com.desk360.livechat.R
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.manager.LiveChatHelper
import io.reactivex.Observable
import java.util.*

class GetConversationListUseCase(private val types: IntArray) :
    BaseUseCase<ArrayList<HeaderChatScreenModel>>() {

    val repository = ConversationRepository.getInstance()

    override fun buildUseCaseObservable(): Observable<ArrayList<HeaderChatScreenModel>>? {
        return repository?.getConversations(types)?.map {
            val list = arrayListOf<HeaderChatScreenModel>()
            it.forEach {
                list.add(convert(it))
            }
            return@map list
        }?.toObservable()
    }

    private fun convert(item: ConversationItem) = HeaderChatScreenModel(
        id = item.conversationEntity?.id ?: "",
        companyLogo = item.conversationEntity?.avatar,
        isAvatarExists = item.conversationEntity?.avatar?.isNotEmpty() == true || LiveChatHelper.settings?.data?.chatbot == true,
        title = if (LiveChatHelper.settings?.data?.chatbot == true) "Chat Bot" else item.conversationEntity?.name,
        initial = getInitial(item.conversationEntity?.name),
        titleColor = LiveChatHelper.settings?.data?.config?.general?.headerTitleColor,
        titleBackgroundColor = LiveChatHelper.settings?.data?.config?.general?.backgroundHeaderColor,
        lastMessageType = item.messageType,
        lastMessageDrawable = getLastMessageDrawable(item.messageType),
        lastMessageBody = getLastMessage(item.messageType, item.messageBody)
    )

    private fun getLastMessage(messageType: Int?, messageBody: String?): String? {
        return when (messageType) {
            ChatUtils.Message.TEXT -> messageBody
            ChatUtils.Message.PHOTO -> LiveChatHelper.settings?.data?.language?.photo
            ChatUtils.Message.VIDEO -> LiveChatHelper.settings?.data?.language?.video
            ChatUtils.Message.DOCUMENT -> LiveChatHelper.settings?.data?.language?.document
            else -> ""
        }
    }

    private fun getLastMessageDrawable(messageType: Int?): Int? {
        return when (messageType) {
            ChatUtils.Message.PHOTO -> R.drawable.ic_image
            ChatUtils.Message.VIDEO -> R.drawable.ic_video
            ChatUtils.Message.DOCUMENT -> R.drawable.ic_document
            else -> null
        }
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