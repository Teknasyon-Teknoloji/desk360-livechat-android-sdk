package com.desk360.base.util

import androidx.annotation.IntDef

object ChatUtils {

    object Conversation {
        const val LIVE_CHAT = 0
        const val CHAT_BOT = 1
        const val ONE_2_ONE_CHAT = 2
        const val GROUP_CHAT = 3

        @IntDef(
            LIVE_CHAT,
            CHAT_BOT,
            ONE_2_ONE_CHAT,
            GROUP_CHAT
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class Type
    }

    object Message {
        // rezerved 0-10 for list attr.
        const val TIME = 0
        const val TEXT = 10
        const val PHOTO = 20
        const val VIDEO = 30
        const val DOCUMENT = 40

        @IntDef(TIME, TEXT, PHOTO, VIDEO, DOCUMENT)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Type
    }

    object Status {
        const val SENDING = 0
        const val ERROR = 1
        const val SENT = 2
        const val FORWARDED = 3
        const val SEEN = 4

        @IntDef(
            SENDING,
            ERROR,
            SENT,
            FORWARDED,
            SEEN
        )
        @Retention(AnnotationRetention.SOURCE)
        annotation class Type
    }
}