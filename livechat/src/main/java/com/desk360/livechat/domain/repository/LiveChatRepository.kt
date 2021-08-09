package com.desk360.livechat.domain.repository

import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.data.model.message.ChatbotsMessageRequest
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.data.model.session.EndSessionRequest
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.data.network.LiveChatRetrofitFactory
import com.desk360.livechat.manager.Desk360LiveChat
import okhttp3.MultipartBody

class LiveChatRepository private constructor() {
    private val service = LiveChatRetrofitFactory.instance.chatService

    fun getConfig(languageCode: String) = service?.getConfig(languageCode, Desk360LiveChat.manager?.token)

    fun createSession(request: SessionRequest) = service?.createSession(request)

    fun endSession(request: EndSessionRequest) = service?.endSession(request)

    fun createChatbotsSession(request: SessionRequest) = service?.createChatbotsSession(request)

    fun sendChatbotsMessage(request: ChatbotsMessageRequest) = service?.sendChatbotsMessage(request)

    fun sendMessage(request: MessageRequest) = service?.sendMessage(request)

    fun feedback(request: FeedbackRequest) = service?.feedback(request)

    fun uploadMedia(attachment: MultipartBody.Part) = service?.uploadMedia(attachment)

    companion object {
        private var INSTANCE: LiveChatRepository? = null
        val instance: LiveChatRepository
            get() {
                if (INSTANCE == null) {
                    INSTANCE = LiveChatRepository()
                }
                return INSTANCE!!
            }
    }
}