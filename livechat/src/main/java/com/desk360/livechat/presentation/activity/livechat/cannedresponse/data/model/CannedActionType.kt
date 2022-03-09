package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model

import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.CannedResponseObject

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
        val time:String = CannedResponseObject.getTime()
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
