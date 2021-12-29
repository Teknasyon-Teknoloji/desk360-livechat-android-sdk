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


        val settings = mapOf("name" to "Luke", "age" to "23")


        val liveChatManager = LiveChatManager.Builder()
            .setDomainAddress("https://domain.desk360.com")
            .setToken("token")
            .setLanguageCode("tr")
            .setSmartPlug(settings)
            .build()

        Desk360LiveChat.init(this, liveChatManager) { isActive ->

            findViewById<View>(R.id.fab_live_chat).visibility =
                if (isActive) View.VISIBLE else View.GONE
        }

        findViewById<View>(R.id.fab_live_chat).setOnClickListener {
            Desk360LiveChat.start()
        }
    }
}