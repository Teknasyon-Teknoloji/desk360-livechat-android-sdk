package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.response

import com.google.gson.annotations.SerializedName


data class CannedResponseButton(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String? = null,
    @SerializedName("actionable_type") var actionableType: String,
    @SerializedName("actionable_id") var actionableId: Int,
    @SerializedName("order_id") var orderId: Int,
    @SerializedName("content") var content: String
)
