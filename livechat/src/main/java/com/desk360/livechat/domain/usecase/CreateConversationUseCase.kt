package com.desk360.livechat.domain.usecase

import com.desk360.base.data.entity.ConversationEntity
import com.desk360.base.domain.repository.ConversationRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.ChatUtils
import com.desk360.base.util.DateUtils
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import io.reactivex.Observable

class CreateConversationUseCase(private val result: HeaderChatScreenModel) : BaseUseCase<String>() {
    private val repository = ConversationRepository.getInstance()

    override fun buildUseCaseObservable(): Observable<String>? {
        val entity = ConversationEntity(
            id = "${LiveChatFirebaseHelper.userId}_l_${result.id}",
            companionId = result.id,
            name = result.title,
            avatar = result.companyLogo,
            type = ChatUtils.Conversation.LIVE_CHAT,
            createdTime = DateUtils.now()
        )

        return repository?.insert(entity)?.map {
            return@map entity.id
        }
    }
}