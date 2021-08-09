package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseFirebaseUseCase
import com.desk360.livechat.data.HeaderChatScreenModel
import com.desk360.livechat.data.mapper.SessionMapper
import com.desk360.livechat.manager.LiveChatFirebaseHelper

class GetSessionUseCase : BaseFirebaseUseCase<HeaderChatScreenModel?>(SessionMapper()) {

    override val rootNode: String
        get() = "messages/${LiveChatFirebaseHelper.userId}/session"
}