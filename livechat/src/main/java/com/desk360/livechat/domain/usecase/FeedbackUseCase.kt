package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.data.model.feedback.FeedbackRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import io.reactivex.Observable

class FeedbackUseCase(private val request: FeedbackRequest) :
    BaseUseCase<Boolean>() {

    override fun buildUseCaseObservable(): Observable<Boolean>? {
        return LiveChatRepository.instance.feedback(request)?.map { response ->
            return@map response.meta?.success
        }
    }
}