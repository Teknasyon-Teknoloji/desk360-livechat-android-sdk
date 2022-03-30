package com.desk360.livechat.manager

import com.desk360.base.util.DateUtils
import com.desk360.livechat.data.model.cannedresponse.CannedPayloadModel
import com.desk360.livechat.data.model.cannedresponse.response.CannedResponse
import com.desk360.livechat.data.model.cannedresponse.response.CannedResponseButton
import com.desk360.livechat.data.model.cannedresponse.response.CannedResponseCloseMenu
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object CannedResponseHelper {

    val cannedResponseModel = LiveChatHelper.settings?.data?.cannedResponse
    var payloadLogData: ArrayList<CannedPayloadModel> = arrayListOf()

    fun getPathId(): Int? = getButton(payloadLogData[payloadLogData.size - 1].id)?.actionableId
    fun clearPayload() = payloadLogData.clear()

    private var cannedReplyButtons: ArrayList<CannedResponseButton> = arrayListOf()
    private var cannedResponseSurvey: CannedResponse? = null
    private var cannedResponseMenuButton: ArrayList<CannedResponseCloseMenu> = arrayListOf()

    fun modelInitiate() {
        setReplyButtons()
        setCloseMenuButton()
        setSurvey()
    }

    private fun setCloseMenuButton() {
        cannedResponseModel?.forEach {
            it.groupedUnits?.closeMenuButton?.forEach { i ->
                cannedResponseMenuButton.add(i)
            }
        }
    }

    private fun setReplyButtons() {
        cannedResponseModel?.forEach {
            it.groupedUnits?.button?.forEach { i ->
                cannedReplyButtons.add(i)
            }
        }
    }

    private fun setSurvey() {
        cannedResponseModel?.forEach {
            if (it.actionableType == "Close_and_survey") cannedResponseSurvey = it
        }
    }

    fun getButton(id: Int): CannedResponseButton? {
        cannedReplyButtons.find { it.id == id }?.let { data ->
            return data
        } ?: return null
    }

    fun getMenuButton(type: String): CannedResponseCloseMenu? {
        cannedResponseMenuButton.find { it.actionableType == type }?.let { data ->
            return data
        } ?: return null
    }

    fun getTime(): String {
        val timeFormat = SimpleDateFormat("HH:mm")
        return timeFormat.format(Date())
    }

    fun addPayloadLog(id: Int) {
        payloadLogData.add(CannedPayloadModel(id, DateUtils.now()))
    }
}