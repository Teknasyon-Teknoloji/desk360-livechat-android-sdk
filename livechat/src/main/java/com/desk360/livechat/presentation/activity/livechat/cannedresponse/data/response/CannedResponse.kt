package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.response

import com.google.gson.annotations.SerializedName

data class CannedResponse(
    @SerializedName("id") var id: Int? = null,
    @SerializedName("actionable_type") var actionableType: String? = null,
    @SerializedName("is_start") var isStart: Int? = null,
    @SerializedName("grouped_units") var groupedUnits: GroupedUnits? = GroupedUnits()
)
