package com.desk360.livechat.data.model.cannedresponse.response

import com.google.gson.annotations.SerializedName

data class CannedResponseCloseMenu(
    @SerializedName("id") var id: Int,
    @SerializedName("type") var type: String? = null,
    @SerializedName("actionable_type") var actionableType: String,
    @SerializedName("actionable_id") var actionableId: Int? = null,
    @SerializedName("order_id") var orderId: Int,
    @SerializedName("icon") var icon:String,
    @SerializedName("content") var content: String
)
