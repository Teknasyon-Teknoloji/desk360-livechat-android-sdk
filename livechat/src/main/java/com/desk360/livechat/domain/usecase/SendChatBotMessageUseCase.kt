package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.data.model.message.ChatbotsMessageRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.LiveChatFirebaseHelper
import com.desk360.livechat.manager.LiveChatHelper
import io.reactivex.Observable

class SendChatBotMessageUseCase(private val request: ChatbotsMessageRequest) :
    BaseUseCase<Boolean?>() {

    override fun buildUseCaseObservable(): Observable<Boolean?>? {
        return LiveChatRepository.instance.sendChatbotsMessage(request)?.map { response ->
            return@map response.meta?.success
        }
    }

    companion object {
        fun createRequest(message: String) = ChatbotsMessageRequest(
            sessionKey = LiveChatFirebaseHelper.userId!!,
            message = message,
            languageCode = LiveChatHelper.settings?.data?.chatbotSettings?.defaultLanguages
                ?: ""
        )
    }
}