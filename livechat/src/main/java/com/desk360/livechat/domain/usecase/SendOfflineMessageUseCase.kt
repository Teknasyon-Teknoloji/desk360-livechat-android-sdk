package com.desk360.livechat.domain.usecase

import com.desk360.base.domain.usecase.BaseUseCase
import com.desk360.livechat.data.model.chatsettings.CustomField
import com.desk360.livechat.data.model.message.MessageRequest
import com.desk360.livechat.domain.repository.LiveChatRepository
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.CannedResponseHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Observable


class SendOfflineMessageUseCase(
    private val request: MessageRequest,
    private val activeCustomField: MutableList<CustomField>
) : BaseUseCase<Boolean>() {

    override fun buildUseCaseObservable(): Observable<Boolean>? {
        request.settings = Desk360LiveChat.manager?.smartPlug
        val jsonElement = Gson().toJsonTree(request.settings)
        val jsonObj = JsonObject()
        jsonObj.apply {
            addProperty("name", request.name)
            addProperty("email", request.email)
            addProperty("message", request.message)
            addProperty("source", request.source)
            activeCustomField.forEach {
                this.addProperty(it.key, it.value)
            }
            request.pathId?.let { addProperty("path_id",it) }
        }.add("settings", jsonElement)


        CannedResponseHelper.payloadLogData.takeIf { it.isNotEmpty() }?.let {
            request.payload = it
            val jsonElementPayload = Gson().toJsonTree(request.payload)
            jsonObj.add("canned_response_payload",jsonElementPayload)
        }

        return LiveChatRepository.instance.sendMessage(jsonObj)?.map { response ->
            return@map response.meta?.success
        }
    }
}