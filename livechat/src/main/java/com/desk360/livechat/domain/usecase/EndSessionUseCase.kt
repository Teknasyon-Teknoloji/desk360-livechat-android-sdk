package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.repository.ConversationRepository
import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.data.model.session.EndSessionRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EndSessionUseCase(
    private val conversationId: String?,
    private val request: EndSessionRequest = EndSessionRequest()
) :
    BaseUseCase<Boolean>() {
    private val conversationRepository = ConversationRepository.getInstance()

    override fun buildUseCaseObservable(): Observable<Boolean>? {
        return LiveChatRepository.instance.endSession(request)?.doOnNext {
            LiveChatSharedPrefManager.clearSession()
            conversationId?.let {
                conversationRepository?.deleteAll()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe()?.let { disposable ->
                        compositeDisposable.add(disposable)
                    }
            }
        }?.doOnError {
            LiveChatSharedPrefManager.clearSession()
            conversationId?.let {
                conversationRepository?.deleteAll()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe()?.let { disposable ->
                        compositeDisposable.add(disposable)
                    }
            }
        }?.map { response ->
            return@map response.meta?.success
        }
    }
}