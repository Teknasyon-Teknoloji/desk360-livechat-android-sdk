package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.data.model.cannedresponse.request.CRRequest
import io.reactivex.Observable

class CannedResponseUseCase(private val request: CRRequest) : BaseUseCase<Unit>() {
    override fun buildUseCaseObservable(): Observable<Unit>? {
        return LiveChatRepository.instance.cannedResponse(request)
    }
}