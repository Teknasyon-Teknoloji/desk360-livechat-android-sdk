package com.desk360.livechat.data.model.media

data class MediaModel(
    val url: String? = null,
    val name: String? = null,
    val type: String? = null,
    val aws: Boolean? = null
)

fun MediaModel.getExtension(): String {
    name?.split('.')?.let { nameAndExtension ->
        return if (nameAndExtension.size > 1) nameAndExtension[1] else ""
    }

    return ""
}

fun MediaModel.getShortName(): String {
    name?.split('.')?.let { nameAndExtension ->
        return if (nameAndExtension[0].length > 13) {
            "${nameAndExtension[0].substring(0, 10)}... .${nameAndExtension[1]}"
        } else name
    }

    return ""
}
