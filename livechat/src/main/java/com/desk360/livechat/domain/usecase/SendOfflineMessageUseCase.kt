package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import io.reactivex.Observable

class SendOfflineMessageUseCase(private val request: MessageRequest) :
    BaseUseCase<Boolean>() {

    override fun buildUseCaseObservable(): Observable<Boolean>? {
        return LiveChatRepository.instance.sendMessage(request)?.map { response ->
            return@map response.meta?.success
        }
    }
}