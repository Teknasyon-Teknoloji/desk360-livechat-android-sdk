package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.desk360.livechat.R
import com.desk360.livechat.presentation.activity.BaseChatActivity

class LiveChatActivity : BaseChatActivity(), PopupMenu.OnMenuItemClickListener {

    override fun initUI() {
        super.initUI()

        binding.toolbarHeader.imageViewMenu.setOnClickListener {
            initMenu(it)
        }
    }

    override fun initObservers() {
        super.initObservers()

        viewModel.isEndSessionSuccessful.observe(this, { isSuccessful ->
            if (isSuccessful) {
                startActivity(Intent(this, FeedbackChatActivity::class.java))
                finish()
            }
        })
    }

    private fun initMenu(view: View) {
        popupMenu = PopupMenu(this, view)
        popupMenu?.setOnMenuItemClickListener(this)
        popupMenu?.inflate(R.menu.live_chat_menu)
        popupMenu?.menu?.findItem(R.id.item_end_chat)?.title =
            viewModel.language.value?.onlineEndChat
        popupMenu?.show()


    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.item_send_transcript -> {
                startActivity(Intent(this, SentTranscriptionChatActivity::class.java))
                finish()
                true
            }
            R.id.item_end_chat -> {
                viewModel.endSession()
                true
            }
            else -> false
        }
    }
}