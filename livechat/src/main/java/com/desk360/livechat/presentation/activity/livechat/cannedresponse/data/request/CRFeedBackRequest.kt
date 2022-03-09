package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.request

import com.google.gson.annotations.SerializedName

data class CRFeedBackRequest(
    @SerializedName("feedback") val feedBack: Int,
    @SerializedName("path_id") val pathId:Int?,
    @SerializedName("canned_response_payload") val payloadData: ArrayList<HashMap<Int,String>>
)
