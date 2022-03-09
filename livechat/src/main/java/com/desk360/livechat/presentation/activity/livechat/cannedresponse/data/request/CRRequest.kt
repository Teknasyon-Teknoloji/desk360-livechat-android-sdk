package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.request

import com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model.CannedPayloadModel
import com.google.gson.annotations.SerializedName

data class CRRequest(
    @SerializedName("canned_response_payload") var payload:ArrayList<CannedPayloadModel>? = arrayListOf(),
    @SerializedName("path_id") var pathId:Int? = null,
    @SerializedName("source") var source: String
)
