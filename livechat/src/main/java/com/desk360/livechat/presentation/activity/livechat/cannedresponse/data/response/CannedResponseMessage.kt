package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.response

import com.google.gson.annotations.SerializedName

data class CannedResponseMessage(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String? = null,
    @SerializedName("actionable_type") var actionableType: String? = null,
    @SerializedName("actionable_id") var actionableId: String? = null,
    @SerializedName("order_id") var orderId: Int? = null,
    @SerializedName("content") var content: String
)
