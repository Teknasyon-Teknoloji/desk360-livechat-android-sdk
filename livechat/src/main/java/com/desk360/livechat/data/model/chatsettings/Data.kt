package com.desk360.livechat.data.model.chatsettings

import com.desk360.livechat.data.model.cannedresponse.response.CannedResponse
import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("default_brand_logo") val defaultBrandLogo: String? = null,
    @SerializedName("default_avatar") val defaultAvatar: String? = null,
    @SerializedName("application_logo") val applicationLogo: String? = null,
    @SerializedName("application_name") val applicationName: String? = null,
    @SerializedName("chatbot") val chatbot: Boolean? = null,
    @SerializedName("chatbot_settings") val chatbotSettings: ChatbotSettings? = null,
    @SerializedName("company_id") val companyId: Int? = null,
    @SerializedName("config") val config: Config? = null,
    @SerializedName("firebase_config") val firebaseConfig: FirebaseConfig? = null,
    @SerializedName("language") val language: Language? = null,
    @SerializedName("triggers") val triggers: List<Trigger>? = null,
    @SerializedName("canned_response") val cannedResponse: ArrayList<CannedResponse>? = null,
    @SerializedName("is_active_canned_response") val isCannedResponse: Boolean? = null,
    @SerializedName("application_id") val applicationId: Int? = null
)