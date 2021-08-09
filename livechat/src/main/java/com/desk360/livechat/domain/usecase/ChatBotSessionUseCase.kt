package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import io.reactivex.Observable

class ChatBotSessionUseCase(private val request: SessionRequest) : BaseUseCase<String?>() {

    override fun buildUseCaseObservable(): Observable<String?>? {
        return LiveChatRepository.instance.createChatbotsSession(request)
            ?.doOnNext { response ->
                SharedPreferencesManager.setUser(request.name, request.email)
                response?.data?.token?.let { token ->
                    SharedPreferencesManager.token = token
                }
            }
            ?.map { response ->
                return@map response.data?.token
            }
    }
}