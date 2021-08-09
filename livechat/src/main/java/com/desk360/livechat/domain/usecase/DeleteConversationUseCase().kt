package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.repository.ConversationRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import io.reactivex.Observable

class DeleteConversationUseCase : BaseUseCase<Boolean>() {
    private val repository = ConversationRepository.getInstance()

    override fun buildUseCaseObservable(): Observable<Boolean>? {
        return repository?.deleteAll()?.doOnNext {
            LiveChatSharedPrefManager.clearSession()
        }?.map {
            return@map true
        }
    }
}