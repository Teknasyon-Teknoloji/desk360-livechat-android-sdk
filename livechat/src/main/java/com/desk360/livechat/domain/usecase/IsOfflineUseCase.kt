package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseFirebaseUseCase
import com.desk360.livechat.data.mapper.OffLineMapper
import com.desk360.livechat.manager.LiveChatHelper

class IsOfflineUseCase : BaseFirebaseUseCase<Boolean>(OffLineMapper()) {

    override val rootNode: String
        get() = "count/${LiveChatHelper.settings?.data?.companyId}/online"
}