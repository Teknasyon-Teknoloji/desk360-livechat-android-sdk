package com.desk360.livechat.data.model.cannedresponse

import com.desk360.livechat.manager.CannedResponseHelper

sealed class CannedActionType {
    data class CRButton(
        val id: Int,
        val actionId: Int,
        val content: String,
        val orderId: Int,
        val actionableType: String,
        var isClickable:Boolean = true,
        var isSelected:Boolean = false
    ) : CannedActionType()

    data class CRMessage(
        val id: Int?,
        val content: String,
        val isMine:Boolean = false,
        val time:String = CannedResponseHelper.getTime()
    ) : CannedActionType()

    data class CRMenuButton(
        val id: Int,
        val actionableType: String,
        val icon: String,
        val orderId: Int,
        val content: String,
        var isClickable:Boolean = true,
        var isSelected:Boolean = false
    ) : CannedActionType()

    data class CRSurvey(
        val id: Int,
        val content: String,
        val actionableType: String
    ) : CannedActionType()
}
