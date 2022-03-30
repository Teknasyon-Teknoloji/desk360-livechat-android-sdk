package com.desk360.livechat.data.model.cannedresponse.response

import com.google.gson.annotations.SerializedName

data class GroupedUnits(
    @SerializedName("Button") var button: ArrayList<CannedResponseButton>? = null,
    @SerializedName("Message") var message: ArrayList<CannedResponseMessage>? = null,
    @SerializedName("Close_menu_button") var closeMenuButton: ArrayList<CannedResponseCloseMenu>? = null
)
