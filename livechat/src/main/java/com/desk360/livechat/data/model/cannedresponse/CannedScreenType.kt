package com.desk360.livechat.data.model.cannedresponse

sealed class CannedScreenType {
    data class LoginScreen(val pathId:Int?) : CannedScreenType()
    object OnBackScreen : CannedScreenType()
    object OnSurveyInfoScreen : CannedScreenType()
}

