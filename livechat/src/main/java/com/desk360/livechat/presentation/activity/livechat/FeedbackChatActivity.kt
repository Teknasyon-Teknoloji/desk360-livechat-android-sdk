package com.desk360.livechat.presentation.activity.livechat

import android.content.Intent
import com.desk360.livechat.databinding.ActivityFeedbackChatBinding
import com.desk360.livechat.presentation.activity.BaseActivity
import com.desk360.livechat.R
import com.desk360.livechat.manager.LiveChatHelper
import com.desk360.livechat.presentation.activity.BaseChatActivity

class FeedbackChatActivity : BaseActivity<ActivityFeedbackChatBinding, FeedbackChatViewModel>() {
    override fun getLayoutResId() = R.layout.activity_feedback_chat

    override fun getViewModelClass() = FeedbackChatViewModel::class.java

    override fun initUI() {
        binding.viewModel = viewModel

        binding.imageViewLike.setOnClickListener {
            viewModel.feedback(FeedbackChatViewModel.Type.LIKE)
        }

        binding.imageViewUnlike.setOnClickListener {
            viewModel.feedback(FeedbackChatViewModel.Type.UNLIKE)
        }

        binding.textViewStartNewChatBg.setOnClickListener {
            if (viewModel.isRunningProgress.value != true) {
                viewModel.isRunningProgress.value = false
                if (viewModel.isAutoLoginControl() && !LiveChatHelper.isOffline)
                    viewModel.autoLogin()
                else {
                    startActivity(Intent(this, LoginNewChatActivity::class.java))
                    finish()
                }
            }
        }

        binding.toolbar.imageViewBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun initObservers() {
        viewModel.conversationDeskId.observe(this, { conversationId ->
            if (conversationId.isNotEmpty()) {
                finish()
                startActivity(Intent(this, LiveChatActivity::class.java).apply {
                    putExtra(BaseChatActivity.EXTRA_CONVERSATION_ID, conversationId)
                })
            }
        })
    }
}