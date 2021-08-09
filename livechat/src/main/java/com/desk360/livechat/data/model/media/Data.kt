package com.desk360.livechat.data.model.media

import java.io.Serializable

data class Data(
    val images: List<MediaModel>? = null,
    val videos: List<MediaModel>? = null,
    val files: List<MediaModel>? = null
) : Serializable
