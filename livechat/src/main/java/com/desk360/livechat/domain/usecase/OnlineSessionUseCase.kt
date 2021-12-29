package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.data.model.session.SessionRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.Desk360LiveChat
import io.reactivex.Observable

class OnlineSessionUseCase(private val request: SessionRequest) : BaseUseCase<String?>() {
    val repository = LiveChatRepository.instance

    override fun buildUseCaseObservable(): Observable<String?>? {
        request.deviceId = Desk360LiveChat.manager?.deviceId
        request.pushToken = Desk360LiveChat.manager?.pushToken
        request.settings = Desk360LiveChat.manager?.smartPlug

        return repository.createSession(request)
            ?.doOnNext { response ->
                SharedPreferencesManager.setUser(request.name, request.email)
                SharedPreferencesManager.token = response?.data?.token

            }
            ?.map { response ->
                return@map response.data?.token
            }
    }
}