package com.desk360.livechat.presentation.activity.livechat.cannedresponse.domain

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.request.CRRequest
import io.reactivex.Observable

class CannedResponseUseCase(private val request: CRRequest) : BaseUseCase<Unit>() {
    override fun buildUseCaseObservable(): Observable<Unit>? {
        return LiveChatRepository.instance.cannedResponse(request)
    }
}