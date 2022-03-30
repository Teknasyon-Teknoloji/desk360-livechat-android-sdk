package com.desk360.livechat.data.model.feedback

import com.desk360.base.manager.SharedPreferencesManager
import com.desk360.livechat.data.model.cannedresponse.CannedPayloadModel
import com.google.gson.annotations.SerializedName

data class FeedbackRequest(
    @SerializedName("feedback") var feedback: Int? = null,
    @SerializedName("session_firebase_key") var userId: String? = SharedPreferencesManager.userId,
    @SerializedName("canned_response_payload") var payload:ArrayList<CannedPayloadModel>? = arrayListOf(),
    @SerializedName("path_id") var pathId:Int? = null,
    @SerializedName("source") var source:String? = null
)
