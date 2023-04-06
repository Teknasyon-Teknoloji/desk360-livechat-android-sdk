package com.desk360.livechat.data.model.session

import com.desk360.livechat.data.model.cannedresponse.CannedPayloadModel
import com.google.gson.annotations.SerializedName

data class SessionRequest(
    val name: String? = "",
    val email: String? = "",
    @SerializedName("uuid")
    var deviceId: String? = "",
    @SerializedName("push_token")
    var pushToken: String? = "",
    private val source: String = "Android",
    @SerializedName("settings")
    var settings: Map<String, String>? = mapOf(),
    @SerializedName("path_id")
    var pathId: Int? = null,
    @SerializedName("canned_response_payload")
    var payload: ArrayList<CannedPayloadModel>? = arrayListOf(),
)