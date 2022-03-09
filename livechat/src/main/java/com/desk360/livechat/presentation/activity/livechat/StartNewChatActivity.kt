package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.desk360.livechat.R
import com.desk360.livechat.databinding.ActivityStartNewChatBinding
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.manager.LiveChatSharedPrefManager
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.presentation.activity.livechat.cannedresponse.presentation.CannedResponseActivity
import com.desk360.livechat.presentation.adapter.ConversationListAdapter

class StartNewChatActivity : BaseActivity<ActivityStartNewChatBinding, StartNewChatViewModel>() {

    private var conversationListAdapter: ConversationListAdapter? = null

    override fun getLayoutResId() = R.layout.activity_start_new_chat

    override fun getViewModelClass() = StartNewChatViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel

        binding.textViewStartNewChatBg.onClick {
            if (LiveChatSharedPrefManager.isNeedNewToken()) {
                if (LiveChatHelper.settings?.data?.isCannedResponse == true) startActivity(
                    Intent(
                        this,
                        CannedResponseActivity::class.java
                    )
                )
                else startActivity(Intent(this, LoginNewChatActivity::class.java))
            } else {
                startActivity(Intent(this, LiveChatActivity::class.java))
            }
        }

        binding.toolbar.imageViewClose.onClick {
            finish()
        }

        initConversationList()
    }

    private fun initConversationList() {
        conversationListAdapter = ConversationListAdapter(this)
        binding.recyclerViewConversations.apply {
            layoutManager =
                LinearLayoutManager(this@StartNewChatActivity, LinearLayoutManager.VERTICAL, false)
            adapter = conversationListAdapter
        }
    }

    override fun initObservers() {
        viewModel.conversations.observe(this, {
            conversationListAdapter?.submitList(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDatas()
    }
}