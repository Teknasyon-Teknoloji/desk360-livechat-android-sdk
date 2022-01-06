package com.desk360.android_live_chat_example

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.desk360.android_live_chat_sdk.R
import com.desk360.livechat.manager.Desk360LiveChat
import com.desk360.livechat.manager.LiveChatManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnLiveChat = findViewById<View>(R.id.fab_live_chat)

        val settings = mapOf("age" to "23")

        val liveChatManager = LiveChatManager.Builder()
            .setDomainAddress("https://domain.desk360.com")
            .setToken("token")
            .setLanguageCode("tr")
            .setUserName("Luke")
            .setUserEmailAddress("luke@emailadress.com")
            .setSmartPlug(settings)
            .build()

        Desk360LiveChat.init(this, liveChatManager) { isActive ->
            btnLiveChat.visibility = if (isActive)
                View.VISIBLE
            else
                View.GONE
        }

        btnLiveChat.setOnClickListener {
            Desk360LiveChat.start()
        }
    }
}