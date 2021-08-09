package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.base.util.Utils
import com.desk360.livechat.data.model.chatsettings.ChatSettingsResponse
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.LiveChatHelper
import io.reactivex.Observable

class ConfigUseCase(private val languageCode: String?) : BaseUseCase<ChatSettingsResponse>() {

    override fun buildUseCaseObservable(): Observable<ChatSettingsResponse>? {
        return LiveChatRepository.instance.getConfig(languageCode ?: Utils.DEFAULT_LANGUAGE_CODE)
            ?.doOnNext { response ->
                LiveChatHelper.settings = response
            }
    }
}