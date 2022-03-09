package com.desk360.livechat.presentation.activity.livechat.cannedresponse.data.model

sealed class CannedScreenType {
    object OfflineScreen : CannedScreenType()
    object OnlineScreen : CannedScreenType()
    object OnBackScreen : CannedScreenType()
    object OnSurveyInfoScreen : CannedScreenType()
}

