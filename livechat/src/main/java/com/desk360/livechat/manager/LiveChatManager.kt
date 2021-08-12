package com.desk360.livechat.manager

import android.content.Context
import android.content.Intent
import androidx.annotation.NonNull
import com.desk360.base.util.Utils
import com.desk360.livechat.data.model.chatsettings.FirebaseConfig
import com.desk360.livechat.domain.usecase.ConfigUseCase
import com.desk360.livechat.manager.LiveChatFirebaseHelper.FIREBASE_NAME
import com.desk360.livechat.presentation.activity.livechat.StartNewChatActivity
import com.google.android.gms.common.internal.Preconditions.checkNotEmpty
import com.google.firebase.FirebaseOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize

class LiveChatManager internal constructor(builder: Builder) : BaseChatManager() {
    init {
        token = builder.token
        domainAddress = builder.domainAddress
        userName = builder.userName
        userEmailAddress = builder.userEmailAddress
        languageCode = builder.languageCode
        deviceId = builder.deviceId
        pushToken = builder.pushToken
    }

    class Builder {
        internal var token: String = ""
        internal var domainAddress: String = ""
        internal var languageCode: String = ""
        internal var userName: String = ""
        internal var userEmailAddress: String = ""
        internal var deviceId: String? = ""
        internal var pushToken: String? = ""

        /**
         * token
         */
        @NonNull
        fun setToken(token: String) = apply {
            this.token = checkNotEmpty(token, "token cannot be empty")
        }

        /**
         * Domain Address
         */
        @NonNull
        fun setDomainAddress(domainAddress: String) = apply {
            this.domainAddress = checkNotEmpty(domainAddress, "domain address cannot be empty")
        }

        /**
         * application ID
         */
        @NonNull
        fun setLanguageCode(languageCode: String) = apply {
            this.languageCode = checkNotEmpty(languageCode, "language code cannot be empty")
        }

        /**
         * Default User Name
         */
        @NonNull
        fun setUserName(userName: String) = apply {
            this.userName = userName
        }

        /**
         * Default User mail address
         */
        @NonNull
        fun setUserEmailAddress(userEmailAddress: String) = apply {
            this.userEmailAddress = userEmailAddress
        }

        /**
         * Device Id
         */
        @NonNull
        fun setDeviceId(deviceId: String?) = apply {
            this.deviceId = deviceId
        }

        /**
         * push token
         */
        @NonNull
        fun setPushNotificationToken(pushToken: String?) = apply {
            this.pushToken = pushToken
        }

        @NonNull
        fun build() = LiveChatManager(this)
    }

    override fun init(context: Context, isActive: (Boolean) -> Unit) {
        ConfigUseCase(Desk360LiveChat.manager?.languageCode).execute(
            onSuccess = { response ->
                configure(context, response.data?.firebaseConfig)
                isActive(true)
            },
            onError = {
                Utils.handleError(context, it)
                isActive(false)
            })
    }

    override fun start(context: Context) {
        context.startActivity(Intent(context, StartNewChatActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    private fun configure(context: Context, firebaseConfig: FirebaseConfig?) {
        val options = FirebaseOptions.Builder()
            .setApplicationId(firebaseConfig?.appId ?: "")
            .setApiKey(firebaseConfig?.apiKey ?: "")
            .setProjectId(firebaseConfig?.projectId)
            .setDatabaseUrl(firebaseConfig?.databaseUrl)
            .build()

        if (LiveChatFirebaseHelper.app == null) {
            LiveChatFirebaseHelper.app = Firebase.initialize(context, options, FIREBASE_NAME)
        }
    }
}